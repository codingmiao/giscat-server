package org.wowtools.giscatserver.dataset.sql.expression2sql;

import org.junit.Assert;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.spatial.BboxIntersects;
import org.wowtools.giscat.vector.mbexpression.spatial.GeoIntersects;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;

import java.util.List;

public class Expression2SqlTest {

    private static final Expression2SqlManager expression2SqlManager = new Expression2SqlManager() {

        @Override
        protected Expression2Sql<BboxIntersects> getBboxIntersects() {
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
    };

    @org.junit.Test
    public void test() throws Exception {
        //绑参数测试
        Assert.assertEquals("f1='1' and f2=$1",
                toWherePart("[\"all\", [\"==\", [\"get\", \"f1\"], \"1\"],[\"==\", [\"get\", \"f2\"], \"$1\"]]"));
        //all
        Assert.assertEquals("f1='1' and f2=1",
                toWherePart("[\"all\", [\"==\", [\"get\", \"f1\"], \"1\"],[\"==\", [\"get\", \"f2\"], 1]]"));
        //all
        Assert.assertEquals("f1='1' or f2=1",
                toWherePart("[\"any\", [\"==\", [\"get\", \"f1\"], \"1\"],[\"==\", [\"get\", \"f2\"], 1]]"));
        //case
        Assert.assertEquals("case when f1='a' then '1' when f1='b' then '2' else '0' end=f2",
                toWherePart("[\"==\",[\"case\",[\"==\",[\"get\",\"f1\"],\"a\"],\"1\",[\"==\",[\"get\",\"f1\"],\"b\"],\"2\",\"0\"],[\"get\", \"f2\"]]"));
        //Compare
        Assert.assertEquals("f1<'1'",
                toWherePart("[\"<\", [\"get\", \"f1\"], \"1\"]"));
        Assert.assertEquals("f1<='1'",
                toWherePart("[\"<=\", [\"get\", \"f1\"], \"1\"]"));
        Assert.assertEquals("f1='1'",
                toWherePart("[\"==\", [\"get\", \"f1\"], \"1\"]"));
        Assert.assertEquals("f1>='1'",
                toWherePart("[\">=\", [\"get\", \"f1\"], \"1\"]"));
        Assert.assertEquals("f1>'1'",
                toWherePart("[\">\", [\"get\", \"f1\"], \"1\"]"));
        Assert.assertEquals("f1<>'1'",
                toWherePart("[\"!=\", [\"get\", \"f1\"], \"1\"]"));
        //match
        Assert.assertEquals("case when '1'=f1 then 1 when '2'=f1 then 2 else 0 end",
                toWherePart("[\"match\",[\"get\", \"f1\"],\"1\",1,\"2\",2,0]"));
        //!
        Assert.assertEquals("f1 is null",
                toWherePart("[\"!\", [\"get\", \"f1\"]]"));
    }

    private String toWherePart(String strExpression) {
        Expression expression = Expression.newInstance(strExpression);

        Expression2Sql expression2Sql = expression2SqlManager.getExpression2Sql(expression);
        String wherePart = expression2Sql.convert(expression, expression2SqlManager).str;
        return wherePart;
    }
}
