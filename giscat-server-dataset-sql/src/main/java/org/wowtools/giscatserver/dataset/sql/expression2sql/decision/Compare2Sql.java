package org.wowtools.giscatserver.dataset.sql.expression2sql.decision;

import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.util.ArrayList;

/**
 * 比较函数的抽象实现
 *
 * @author liuyu
 * @date 2023/2/13
 */
abstract class Compare2Sql<T extends Expression<Boolean>> extends Expression2Sql<T> {
    @Override
    public Part convert(T expression, Expression2SqlManager expression2SqlManager) {
        ArrayList expressionArray = expression.getExpressionArray();
        Part p1 = getValue(expressionArray.get(1), expression2SqlManager);
        Part p2 = getValue(expressionArray.get(2), expression2SqlManager);
        StringBuilder sb = new StringBuilder();
        if (p1.single) {
            sb.append(p1.str);
        } else {
            sb.append('(').append(p1.str).append(')');
        }
        sb.append(getSymbol());
        if (p2.single) {
            sb.append(p2.str);
        } else {
            sb.append('(').append(p2.str).append(')');
        }
        return new Part(sb.toString(), true);
    }

    /**
     * 获得比较符号
     *
     * @return 如 > < =
     */
    protected abstract String getSymbol();
}
