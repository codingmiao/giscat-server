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
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 关系型数据库FeatureResultSet
 *
 * @author liuyu
 * @date 2022/8/18
 */
@Slf4j
public abstract class SqlFeatureResultSet implements FeatureResultSet {

    /**
     * 从ResultSet中读取属性
     */
    private final class PropertyReader {
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
     * PreparedStatement设置器，用于绑定参数
     */
    @FunctionalInterface
    interface PreparedStatementSetter {
        void set(PreparedStatement pstm) throws SQLException;
    }

    private final Connection conn;
    private final PreparedStatement pstm;
    private final ResultSet rs;

    private final PropertyReader[] propertyReaders;

    private final int shapeIndex;

    private boolean hasNext;

    public SqlFeatureResultSet(Connection conn, String sql, PreparedStatementSetter preparedStatementSetter, String shapeName) {
        this.conn = conn;
        try {
            pstm = conn.prepareStatement(sql);
            preparedStatementSetter.set(pstm);
            rs = pstm.executeQuery();
        } catch (SQLException e) {
            close();
            throw new RuntimeException(e);
        }
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            propertyReaders = new PropertyReader[columnCount - 1];
            int readerIdx = 0;
            int shapeIndex = -1;
            for (int i = 1; i <= columnCount; i++) {
                String name = metaData.getColumnLabel(i);
                if (shapeIndex == -1 && name.equals(shapeName)) {
                    shapeIndex = i;
                } else {
                    propertyReaders[readerIdx] = new PropertyReader(i, shapeName);
                    readerIdx++;
                }
            }
            if (shapeIndex == -1) {
                throw new RuntimeException("未找到shape字段");
            }
            this.shapeIndex = shapeIndex;

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
            Geometry geometry = readGeometry(rs, shapeIndex);
            Map<String, Object> properties = new HashMap<>(propertyReaders.length);
            for (PropertyReader propertyReader : propertyReaders) {
                properties.put(propertyReader.propertyName, propertyReader.read(rs));
            }
            Feature feature = new Feature(geometry,properties);
            return feature;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 从ResultSet中解析当前行中的geometry对象
     * @param rs ResultSet
     * @param shapeIndex shape字段在第几个，和ResultSet规范一样从1开始
     * @return
     */
    protected abstract Geometry readGeometry(ResultSet rs, int shapeIndex);

}
