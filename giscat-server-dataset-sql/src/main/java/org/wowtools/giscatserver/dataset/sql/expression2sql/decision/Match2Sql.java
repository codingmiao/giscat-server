/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
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
        for (int i = 2; i < n; i += 2) {
            Part label = getValue(expressionArray.get(i), expression2SqlManager);
            Part output = getValue(expressionArray.get(i + 1), expression2SqlManager);
            sb.append("when ").append(label.str).append('=').append(input.str).append(" then ");
            sb.append(output.str).append(" ");
        }
        Part output = getValue(expressionArray.get(n), expression2SqlManager);
        sb.append("else ").append(output.str).append(" end");
        return new Part(sb.toString(), true);
    }
}
