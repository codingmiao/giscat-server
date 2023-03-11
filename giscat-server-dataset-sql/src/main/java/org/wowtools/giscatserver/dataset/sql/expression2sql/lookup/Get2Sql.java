/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql.expression2sql.lookup;

import org.jetbrains.annotations.NotNull;
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
    public Part convert(@NotNull Get expression, @NotNull Expression2SqlManager expression2SqlManager) {
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
