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
import org.wowtools.giscat.vector.mbexpression.spatial.BboxIntersects;
import org.wowtools.giscat.vector.mbexpression.spatial.GeoIntersects;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.util.ArrayList;
import java.util.List;

/**
 * postgis Expression2Sql对象的管理工具
 *
 * @author liuyu
 * @date 2022/9/1
 */
public class PostgisExpression2SqlManager extends Expression2SqlManager {

    @Override
    protected @NotNull Expression2Sql<BboxIntersects> getBboxIntersects() {
        return new Expression2Sql<>() {
            @Override
            public @NotNull Part convert(@NotNull BboxIntersects expression, @NotNull Expression2SqlManager expression2SqlManager) {
                Object o = expression.getExpressionArray().get(1);
                // "shape && ST_MakeEnvelope(?, ?, ?, ?, 4326)";
                ArrayList list = (ArrayList) o;
                StringBuilder sb = new StringBuilder(ShapePlaceholder);
                sb.append(" && ST_MakeEnvelope(");
                sb.append(getValue(list.get(0), expression2SqlManager).str).append(',');
                sb.append(getValue(list.get(1), expression2SqlManager).str).append(',');
                sb.append(getValue(list.get(2), expression2SqlManager).str).append(',');
                sb.append(getValue(list.get(3), expression2SqlManager).str).append(",4326)");
                return new Part(sb.toString(), true);
            }
        };
    }

    @Override
    protected @NotNull Expression2Sql<GeoIntersects> getGeoIntersects() {
        return new Expression2Sql<>() {
            @Override
            public @NotNull Part convert(@NotNull GeoIntersects expression, @NotNull Expression2SqlManager expression2SqlManager) {
                //"ST_intersects(shape,st_geomfromtext('srid=4326;linestring(110 20,112 23)')";
                Object o = expression.getExpressionArray().get(1);
                StringBuilder sb = new StringBuilder("ST_intersects(");
                sb.append(ShapePlaceholder).append(',');
                sb.append("st_geomfromtext(");
                sb.append(getValue(o, expression2SqlManager).str);
                sb.append(",4326))");
                return new Part(sb.toString(), true);
            }
        };
    }

    @Override
    protected @Nullable List<Expression2Sql> getExtends() {
        return null;
    }
}
