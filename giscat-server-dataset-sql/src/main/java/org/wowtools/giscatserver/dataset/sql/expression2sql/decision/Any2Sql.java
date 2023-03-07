/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql.expression2sql.decision;

import org.wowtools.giscat.vector.mbexpression.decision.Any;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.util.ArrayList;

/**
 * @author liuyu
 * @date 2022/8/23
 */
public class Any2Sql extends Expression2Sql<Any> {
    @Override
    public Part convert(Any expression, Expression2SqlManager expression2SqlManager) {
        ArrayList expressionArray = expression.getExpressionArray();
        StringBuilder sb = new StringBuilder();
        int n = expressionArray.size() - 1;
        for (int i = 1; i < n; i++) {
            Object o = expressionArray.get(i);
            Part part = getValue(o, expression2SqlManager);
            if (part.single) {
                sb.append(part.str).append(" or ");
            } else {
                sb.append('(').append(part.str).append(") or ");
            }
        }
        Part part = getValue(expressionArray.get(n), expression2SqlManager);
        if (part.single) {
            sb.append(part.str);
        } else {
            sb.append('(').append(part.str).append(")");
        }
        return new Part(sb.toString(), n <= 1);
    }


}
