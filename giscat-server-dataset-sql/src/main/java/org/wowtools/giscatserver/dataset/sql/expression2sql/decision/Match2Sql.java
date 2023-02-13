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
package org.wowtools.giscatserver.dataset.sql.expression2sql.decision;

import org.wowtools.giscat.vector.mbexpression.decision.Match;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.util.ArrayList;

/**
 * @author liuyu
 * @date 2023/2/13
 */
public class Match2Sql extends Expression2Sql<Match> {
    @Override
    public Part convert(Match expression, Expression2SqlManager expression2SqlManager) {
        // 转为case sql
        ArrayList expressionArray = expression.getExpressionArray();
        Part input = getValue(expressionArray.get(1), expression2SqlManager);

        StringBuilder sb = new StringBuilder("case ");
        int n = expressionArray.size() - 1;
        for (int i = 1; i < n; i += 2) {
            Part label = getValue(expressionArray.get(i), expression2SqlManager);
            Part output = getValue(expressionArray.get(i + 1), expression2SqlManager);
            sb.append("when ").append(label).append('=').append(input.str).append(" then ");
            sb.append(output.str).append(" ");
        }
        Part output = getValue(expressionArray.get(n), expression2SqlManager);
        sb.append("else ").append(output.str).append(" end");
        return new Part(sb.toString(), true);
    }
}
