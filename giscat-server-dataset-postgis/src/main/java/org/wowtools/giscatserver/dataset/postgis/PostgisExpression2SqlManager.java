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
package org.wowtools.giscatserver.dataset.postgis;

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
    protected Expression2Sql<BboxIntersects> getBboxIntersects() {
        return new Expression2Sql<BboxIntersects>() {
            @Override
            public Part convert(BboxIntersects expression, Expression2SqlManager expression2SqlManager) {
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
    protected Expression2Sql<GeoIntersects> getGeoIntersects() {
        return new Expression2Sql<GeoIntersects>() {
            @Override
            public Part convert(GeoIntersects expression, Expression2SqlManager expression2SqlManager) {
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
    protected List<Expression2Sql> getExtends() {
        return null;
    }
}
