/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql.expression2sql.decision;

import org.jetbrains.annotations.NotNull;
import org.wowtools.giscat.vector.mbexpression.decision.Negation;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.util.ArrayList;

/**
 * @author liuyu
 * @date 2023/2/13
 */
public class Negation2Sql extends Expression2Sql<Negation> {
    @Override
    public @NotNull Part convert(@NotNull Negation expression, Expression2SqlManager expression2SqlManager) {
        ArrayList expressionArray = expression.getExpressionArray();
        Part p1 = getValue(expressionArray.get(1), expression2SqlManager);
        StringBuilder sb = new StringBuilder();
        if (p1.single) {
            sb.append(p1.str).append(" is null");
        } else {
            sb.append('(').append(p1.str).append(") is null");
        }
        return new Part(sb.toString(), true);
    }
}
