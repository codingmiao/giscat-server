package org.wowtools.giscatserver.dataset.sql;


import org.junit.Assert;

public class SqlExpressionDialectTest {
    @org.junit.Test
    public void test() {
        Assert.assertEquals("a =?\t paramNames: {$x}, ",
                parse("a =$x"));

        Assert.assertEquals("a ='1' and b = '1''1'\t paramNames: ",
                parse("a ='1' and b = '1''1'"));

        Assert.assertEquals("a = ? and b = ?\t paramNames: {$x}, {$y}, ",
                parse("a = $x and b = $y"));

        Assert.assertEquals("a=? and bb= ? and c =? and d = ?\t paramNames: {$11}, {$12}, {$111}, {$111}, ",
                parse("a=$11 and bb= $12 and c =$111 and d = $111"));

        Assert.assertEquals("a = '$111' and b = '$1' and c = ? and d = '''$11'\t paramNames: {$1}, ",
                parse("a = '$111' and b = '$1' and c = $1 and d = '''$11'"));

    }

    private String parse(String wherePart) {
        SqlExpressionDialect sqlExpressionDialect = new SqlExpressionDialect(wherePart);
        return sqlExpressionDialect.toString();
    }
}
