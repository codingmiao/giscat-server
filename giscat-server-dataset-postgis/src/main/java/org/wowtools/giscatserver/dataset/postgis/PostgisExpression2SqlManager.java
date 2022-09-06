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

import org.wowtools.giscat.vector.mbexpression.spatial.BboxIntersection;
import org.wowtools.giscat.vector.mbexpression.spatial.BboxIntersects;
import org.wowtools.giscat.vector.mbexpression.spatial.GeoIntersection;
import org.wowtools.giscat.vector.mbexpression.spatial.GeoIntersects;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.util.List;

/**
 * postgis Expression2Sql对象的管理工具
 * @author liuyu
 * @date 2022/9/1
 */
public class PostgisExpression2SqlManager extends Expression2SqlManager {
    @Override
    protected Expression2Sql<BboxIntersection> getBboxIntersection() {
        return new Expression2Sql<BboxIntersection>() {
            @Override
            public Part convert(BboxIntersection expression, Expression2SqlManager expression2SqlManager) {
                return null;
            }
        };
    }

    @Override
    protected Expression2Sql<BboxIntersects> getBboxIntersects() {
        return null;
    }

    @Override
    protected Expression2Sql<GeoIntersection> getGeoIntersection() {
        return null;
    }

    @Override
    protected Expression2Sql<GeoIntersects> getGeoIntersects() {
        return null;
    }

    @Override
    protected List<Expression2Sql> getExtends() {
        return null;
    }
}
