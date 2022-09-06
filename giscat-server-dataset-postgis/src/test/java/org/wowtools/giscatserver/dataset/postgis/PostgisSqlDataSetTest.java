package org.wowtools.giscatserver.dataset.postgis;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscatserver.dataconnect.sql.SqlDataConnect;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.SqlExpressionDialect;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PostgisSqlDataSetTest {
/*
测试数据构建
create extension postgis;
create table testline (id serial PRIMARY KEY, name VARCHAR(50), geom geometry(linestring, 4326));
insert into testline (name, geom) values('name1', st_geomfromtext('srid=4326;linestring(100 20,102 23)'));
insert into testline (name, geom) values('name2', st_geomfromtext('srid=4326;linestring(110 20,112 23)'));

#查询sql
#最邻近查询
select t.*,st_astext(geom) from testline t
ORDER BY
geom <-> st_geomfromewkt('srid=4326;point(100 20)')
LIMIT 10;

#

* */

    private PostgisSqlDataSet dataSet;

    @org.junit.Before
    public void init() {
        Properties properties = new Properties();
        //https://github.com/brettwooldridge/HikariCP#gear-configuration-knobs-baby
        properties.putAll(Map.of(
                "jdbcUrl", "jdbc:postgresql://localhost:5432/test",
                "username", "postgres",
                "password", "sde"
        ));
        HikariConfig configuration = new HikariConfig(properties);
        HikariDataSource dataSource = new HikariDataSource(configuration);
        SqlDataConnect dataConnect = new SqlDataConnect(dataSource);
        dataSet = new PostgisSqlDataSet(dataConnect, new PostgisExpression2SqlManager(), "testline", "geom");
    }


    @org.junit.Test
    public void test() {
        Expression expression = Expression.newInstance("[\"==\", [\"get\",\"name\"],\"$name\"]");
        SqlExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);

        ExpressionParams expressionParams = new ExpressionParams(Map.of("$name","name2"));

        FeatureResultSet rs = dataSet.queryByDialect(List.of("name"), expressionDialect, expressionParams);
        rs.forEachRemaining(f->{
            System.out.println(f.getGeometry());
        });
    }
}
