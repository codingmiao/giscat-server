/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.dataset.postgis;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKTWriter;
import org.postgresql.util.PGobject;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscatserver.dataconnect.sql.SqlDataConnect;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.SqlDataSet;
import org.wowtools.giscatserver.dataset.sql.SqlExpressionDialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * postgis关系型数据库数据集
 *
 * @author liuyu
 * @date 2022/9/1
 */
public class PostgisSqlDataSet extends SqlDataSet<PostgisDataSetCtx> {
    /**
     * @param dataConnect           数据集使用的数据连接
     * @param expression2SqlManager 转换sql的管理器
     * @param tableName             数据集对应表名，也可以是一个sql语句，传入sql语句时需要加括号和别名例如"(select a,b from xxx) t"
     * @param shapeName             空间数据字段名
     */
    public PostgisSqlDataSet(String id, SqlDataConnect dataConnect, Expression2SqlManager expression2SqlManager, String tableName, String shapeName) {
        super(id, dataConnect, expression2SqlManager, tableName, shapeName);
    }

    private static final WKTWriter wKTWriter = new WKTWriter();

    @Override
    protected String geometry2sqlObject(Geometry geometry) {
        return wKTWriter.write(geometry);
    }

    @Override
    public @NotNull FeatureResultSet nearestByDialect(@Nullable List<String> propertyNames, @Nullable SqlExpressionDialect expressionDialect, @Nullable ExpressionParams expressionParams, double x, double y, int n) {
        //#最邻近查询
        //select t.*,st_astext(geom) from testline t
        //ORDER BY
        //geom <-> st_geomfromtext('point(100 20)',4326)
        //LIMIT 10;
        String wkt = new StringBuilder("point(").append(x).append(' ').append(y).append(')').toString();
        StringBuilder sbSql = new StringBuilder("select t.* from(select ");
        sbSql.append(shapeName);
        if (null != propertyNames) {
            sbSql.append(",");
            for (String propertyName : propertyNames) {
                sbSql.append(propertyName).append(',');
            }
            sbSql.deleteCharAt(sbSql.length() - 1);
        }

        sbSql.append(" from ").append(tableName);
        if (null != expressionDialect) {
            String wherePart = expressionDialect.getWherePart();
            if (wherePart != null) {
                wherePart = wherePart.trim();
                if (wherePart.length() > 0) {
                    sbSql.append(" where ").append(wherePart);
                }
            }
        }
        sbSql.append(") t ORDER BY ").append(shapeName);
        sbSql.append(" <-> st_geomfromtext(?,4326) LIMIT ?");
        String sql = sbSql.toString();

        PreparedStatementSetter preparedStatementSetter = null == expressionDialect ?
                (pstm) -> {
                    pstm.setObject(1, wkt);
                    pstm.setInt(2, n);
                }
                : (pstm) -> {
            int idx = 1;
            if (null != expressionDialect.getWherePart()) {
                for (String paramName : expressionDialect.getParamNames()) {
                    assert expressionParams != null;
                    Object obj = expressionParams.getValue(paramName);
                    if (ExpressionParams.empty == obj) {
                        obj = null;
                    } else if (obj instanceof Geometry) {
                        obj = geometry2sqlObject((Geometry) obj);
                    }
                    pstm.setObject(idx, obj);
                    idx++;
                }
            }
            pstm.setObject(idx, wkt);
            pstm.setInt(idx + 1, n);
        };

        return new SqlFeatureResultSet(sql, preparedStatementSetter, propertyNames);
    }

    @Override
    protected @NotNull PostgisDataSetCtx createDatSetCtx() {
        return new PostgisDataSetCtx();
    }

    @Override
    protected @Nullable Geometry readGeometry(@NotNull ResultSet rs, int shapeIndex, @NotNull PostgisDataSetCtx ctx) throws SQLException {
        PGobject obj = (PGobject) rs.getObject(shapeIndex);
        if (null == obj) {
            return null;
        }
        String hex = obj.getValue();
        if (null == hex) {
            return null;
        }
        byte[] bytes = WKBReader.hexToBytes(hex);
        Geometry geo;
        try {
            geo = ctx.wkbReader.read(bytes);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return geo;
    }
}
