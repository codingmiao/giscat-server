/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql.expression2sql.decision;

import org.jetbrains.annotations.NotNull;
import org.wowtools.giscat.vector.mbexpression.decision.Case;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.util.ArrayList;

/**
 * @author liuyu
 * @date 2022/8/23
 */
public class Case2Sql extends Expression2Sql<Case> {
    @Override
    public @NotNull Part convert(@NotNull Case expression, Expression2SqlManager expression2SqlManager) {
        ArrayList expressionArray = expression.getExpressionArray();
        StringBuilder sb = new StringBuilder("case ");
        int n = expressionArray.size() - 1;
        for (int i = 1; i < n; i += 2) {
            Part partCondition = getValue(expressionArray.get(i), expression2SqlManager);
            sb.append("when ").append(partCondition.str).append(" then ");

            Part partOutput = getValue(expressionArray.get(i + 1), expression2SqlManager);
            sb.append(partOutput.str).append(" ");
        }
        Part partOutput = getValue(expressionArray.get(n), expression2SqlManager);
        sb.append("else ").append(partOutput.str).append(" end");
        return new Part(sb.toString(), true);
    }


}
