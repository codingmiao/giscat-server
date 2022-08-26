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
package org.wowtools.giscatserver.dataset.sql.expression2sql.lookup;

import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.lookup.Get;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

/**
 * @author liuyu
 * @date 2022/8/23
 */
public class Get2Sql extends Expression2Sql<Get> {
    @Override
    public Part convert(Get expression, Expression2SqlManager expression2SqlManager) {
        Object o = expression.getExpressionArray().get(1);
        if (o instanceof Expression) {
            Expression subExpression = (Expression) o;
            Expression2Sql subExpression2Sql = expression2SqlManager.getExpression2Sql(subExpression);
            return subExpression2Sql.convert(subExpression, expression2SqlManager);
        } else {
            return new Part(String.valueOf(o), true);
        }
    }
}
