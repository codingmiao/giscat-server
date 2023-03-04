/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql.expression2sql;

import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;

/**
 * 表达式转换为sql where字符串，形如"a = $x and b = $y"
 *
 * @author liuyu
 * @date 2022/8/23
 */
public abstract class Expression2Sql<E extends Expression> {

    /**
     * Expression2Sql返回的一部分sql字符串
     */
    public static final class Part {
        /**
         * sql字符串
         */
        public final String str;
        /**
         * 是否是单独的，若否，父级字符串需要括号包围以保证优运算先级不错乱
         */
        public final boolean single;

        public Part(String str, boolean single) {
            this.str = str;
            this.single = single;
        }
    }

    public abstract Part convert(E expression, Expression2SqlManager expression2SqlManager);


    protected Part getValue(Object o, Expression2SqlManager expression2SqlManager) {
        if (o instanceof Expression) {
            Expression subExpression = (Expression) o;
            Expression2Sql subExpression2Sql = expression2SqlManager.getExpression2Sql(subExpression);
            return subExpression2Sql.convert(subExpression, expression2SqlManager);
        } else {
            if (o instanceof String) {
                String str = (String) o;
                if ('$' == str.charAt(0)) {
                    return new Part(str, true);
                } else {
                    str = str.replace("'", "''");
                    return new Part("'" + str + "'", true);
                }
            } else {
                return new Part(String.valueOf(o), true);
            }
        }
    }
}
