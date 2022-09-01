/*****************************************************************
 *  Copyright (c) 2022- "giscat by 刘雨 (https://github.com/codingmiao/giscat)"
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.wowtools.giscatserver.dataset.sql;


import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscatserver.dataconnect.sql.SqlDataConnect;
import org.wowtools.giscatserver.dataset.api.DatSet;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关系型数据库数据集
 */
@Slf4j
public abstract class SqlDatSet extends DatSet<SqlDataConnect, SqlExpressionDialect> {

    private final SqlDataConnect dataConnect;
    private final Expression2SqlManager expression2SqlManager;

    private final String tableName;

    private final String shapeName;

    /**
     * @param dataConnect           数据集使用的数据连接
     * @param expression2SqlManager 转换sql的管理器
     * @param tableName             数据集对应表名，也可以是一个sql语句，传入sql语句时需要加括号和别名例如"(select a,b from xxx) t"
     * @param shapeName             空间数据字段名
     */
    public SqlDatSet(SqlDataConnect dataConnect, Expression2SqlManager expression2SqlManager, String tableName, String shapeName) {
        this.dataConnect = dataConnect;
        this.expression2SqlManager = expression2SqlManager;
        this.tableName = tableName;
        this.shapeName = shapeName;
    }


    /**
     * 将表达式转为sql方言
     *
     * @param expression 表达式
     * @return 方言
     */
    @Override
    public SqlExpressionDialect buildExpressionDialect(Expression<Boolean> expression) {
        Expression2Sql expression2Sql = expression2SqlManager.getExpression2Sql(expression);
        String wherePart = expression2Sql.convert(expression, expression2SqlManager).str;
        SqlExpressionDialect sqlExpressionDialect = new SqlExpressionDialect(wherePart);
        return sqlExpressionDialect;
    }

    @Override
    public FeatureResultSet queryByDialect(List<String> propertyNames, SqlExpressionDialect expressionDialect, ExpressionParams expressionParams) {
        StringBuilder sbSql = new StringBuilder("select ");
        sbSql.append(shapeName).append(",");
        for (String propertyName : propertyNames) {
            sbSql.append(propertyName).append(',');
        }
        sbSql.deleteCharAt(sbSql.length() - 1);
        sbSql.append(" from ").append(tableName);
        sbSql.append(" where ").append(expressionDialect.getWherePart());
        String sql = sbSql.toString();

        PreparedStatementSetter preparedStatementSetter = (pstm) -> {
            int idx = 1;
            for (String paramName : expressionDialect.getParamNames()) {
                Object obj = expressionParams.getValue(paramName);
                if (ExpressionParams.empty == obj) {
                    obj = null;
                }
                pstm.setObject(idx, obj);
                idx++;
            }
        };

        SqlFeatureResultSet sqlFeatureResultSet = new SqlFeatureResultSet(sql, preparedStatementSetter, propertyNames);
        return sqlFeatureResultSet;
    }


    @Override
    public abstract FeatureResultSet nearestByDialect(List<String> propertyNames, SqlExpressionDialect expressionDialect, ExpressionParams expressionParams, double x, double y, int n);


    /**
     * 从ResultSet中解析当前行中的geometry对象
     *
     * @param rs         ResultSet
     * @param shapeIndex shape字段在第几个，和ResultSet规范一样从1开始
     * @return
     */
    protected abstract Geometry readGeometry(ResultSet rs, int shapeIndex);


    /**
     * 从ResultSet中读取属性
     */
    protected class PropertyReader {
        private final int index;
        private final String propertyName;

        public PropertyReader(int index, String propertyName) {
            this.index = index;
            this.propertyName = propertyName;
        }

        public Object read(ResultSet rs) throws SQLException {
            return rs.getObject(index);
        }
    }

    /**
     * 设置PreparedStatement绑定参数之类工具
     */
    @FunctionalInterface
    protected interface PreparedStatementSetter {
        void set(PreparedStatement pstm) throws SQLException;
    }

    protected class SqlFeatureResultSet implements FeatureResultSet {

        private final Connection conn;
        private final PreparedStatement pstm;
        private final ResultSet rs;

        private final PropertyReader[] propertyReaders;

        private boolean hasNext;

        public SqlFeatureResultSet(String sql, PreparedStatementSetter preparedStatementSetter, List<String> propertyNames) {
            this.conn = dataConnect.getConnection();
            try {
                pstm = conn.prepareStatement(sql);
                preparedStatementSetter.set(pstm);
                rs = pstm.executeQuery();
            } catch (SQLException e) {
                log.warn("查询出错, sql:{}", sql);
                close();
                throw new RuntimeException(e);
            }
            try {
                propertyReaders = new PropertyReader[propertyNames.size()];
                int i = 2;// 第一个字段是shape，从第二个字段开始拿
                for (String propertyName : propertyNames) {
                    propertyReaders[i - 2] = new PropertyReader(i, propertyName);
                }

                hasNext = rs.next();

            } catch (Exception e) {
                close();
                throw new RuntimeException("读取ResultSet基本信息出错", e);
            }
        }

        @Override
        public void close() {
            if (null != rs) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    log.warn("关闭jdbc rs异常:", ex);
                }
            }
            if (null != pstm) {
                try {
                    pstm.close();
                } catch (Exception ex) {
                    log.warn("关闭jdbc pstm异常:", ex);
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                } catch (Exception ex) {
                    log.warn("关闭jdbc conn异常:", ex);
                }
            }
        }


        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Feature next() {
            try {
                hasNext = rs.next();
                Geometry geometry = readGeometry(rs, 1);
                Map<String, Object> properties = new HashMap<>(propertyReaders.length);
                for (PropertyReader propertyReader : propertyReaders) {
                    properties.put(propertyReader.propertyName, propertyReader.read(rs));
                }
                Feature feature = new Feature(geometry, properties);
                return feature;
            } catch (SQLException e) {
                close();
                throw new RuntimeException(e);
            }
        }


    }
}
