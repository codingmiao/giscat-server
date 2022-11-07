package org.wowtools.giscatserver.dataset.postgis;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Assert;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTWriter;
import org.wowtools.giscat.util.analyse.Bbox;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscatserver.dataconnect.sql.SqlDataConnect;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;
import org.wowtools.giscatserver.dataset.sql.SqlExpressionDialect;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PostgisSqlDataSetTest {
/*
测试数据构建
create extension postgis;
create table testline (id serial PRIMARY KEY, name VARCHAR(50), geom geometry(linestring, 4326));
insert into testline (name, geom) values('name0', st_geomfromtext('srid=4326;linestring(101 21,102 23)'));
insert into testline (name, geom) values('name1', st_geomfromtext('srid=4326;linestring(100 20,102 23)'));
insert into testline (name, geom) values('name2', st_geomfromtext('srid=4326;linestring(110 20,112 23)'));


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
    public void queryByDialectTest() throws Exception{

        {
            // null
            FeatureResultSet rs = dataSet.queryByDialect(List.of("name"), null, null);
            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(3, res.size());
        }

        {
            // bboxIntersects
            Expression expression = Expression.newInstance("[\"bboxIntersects\", [\"$1\", \"$2\", \"$3\", \"$4\"]]");
            ExpressionParams expressionParams = new ExpressionParams(Map.of(
                    "$1", 100,
                    "$2", 20,
                    "$3", 105,
                    "$4", 25
            ));

            SqlExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);
            FeatureResultSet rs = dataSet.queryByDialect(List.of("name"), expressionDialect, expressionParams);
            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(2, res.size());
            Assert.assertEquals("name0", res.get(0).getProperties().get("name"));
            Assert.assertEquals("name1", res.get(1).getProperties().get("name"));
        }

        {
            // get
            Expression expression = Expression.newInstance("[\"==\", [\"get\",\"name\"],\"$1\"]");
            ExpressionParams expressionParams = new ExpressionParams(Map.of("$1", "name2"));

            SqlExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);
            FeatureResultSet rs = dataSet.queryByDialect(List.of("name"), expressionDialect, expressionParams);
            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(1, res.size());
            Assert.assertEquals("name2", res.get(0).getProperties().get("name"));
        }
        {
            // geoIntersection
            Expression expression = Expression.newInstance("[\"geoIntersects\", \"$1\"]");
            double xmin = 100, xmax = 105, ymin = 20, ymax = 25;
            Polygon geo = new Bbox(xmin, ymin, xmax, ymax).toPolygon(new GeometryFactory());
            ExpressionParams expressionParams = new ExpressionParams(Map.of(
                    "$1", geo
            ));

            SqlExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);
            FeatureResultSet rs = dataSet.queryByDialect(List.of("name"), expressionDialect, expressionParams);
            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(2, res.size());
            Assert.assertEquals("name0", res.get(0).getProperties().get("name"));
            Assert.assertEquals("name1", res.get(1).getProperties().get("name"));
        }

        {
            // bboxIntersects and get
            Expression expression = Expression.newInstance("[\"all\"," +
                    "[\"bboxIntersects\", [\"$1\", \"$2\", \"$3\", \"$4\"]]," +
                    "[\"==\", [\"get\",\"name\"],\"$name\"]" +
                    "]");
            ExpressionParams expressionParams = new ExpressionParams(Map.of(
                    "$1", 100,
                    "$2", 20,
                    "$3", 105,
                    "$4", 25,
                    "$name", "name1"
            ));

            SqlExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);
            FeatureResultSet rs = dataSet.queryByDialect(List.of("name"), expressionDialect, expressionParams);
            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(res.size(), 1);
            Assert.assertEquals("name1", res.get(0).getProperties().get("name"));
        }
    }

    @org.junit.Test
    public void nearestByDialectTest() throws Exception{
        {
            // null
            FeatureResultSet rs = dataSet.nearestByDialect(List.of("name"), null, null, 111, 21, 1);

            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(res.size(), 1);
            Assert.assertEquals("name2", res.get(0).getProperties().get("name"));
        }

        {
            // geoIntersection
            Expression expression = Expression.newInstance("[\"geoIntersects\", \"$1\"]");
            double xmin = 109, xmax = 111, ymin = 21, ymax = 24;
            Polygon geo = new Bbox(xmin, ymin, xmax, ymax).toPolygon(new GeometryFactory());
            ExpressionParams expressionParams = new ExpressionParams(Map.of(
                    "$1", geo
            ));
            SqlExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);


            FeatureResultSet rs = dataSet.nearestByDialect(List.of("name"), expressionDialect, expressionParams, 111, 21, 1);

            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(res.size(), 1);
            Assert.assertEquals("name2", res.get(0).getProperties().get("name"));
        }

        {
            // geoIntersection
            Expression expression = Expression.newInstance("[\"geoIntersects\", \"$1\"]");
            double xmin = 1, xmax = 2, ymin = 1, ymax = 2;
            Polygon geo = new Bbox(xmin, ymin, xmax, ymax).toPolygon(new GeometryFactory());
            ExpressionParams expressionParams = new ExpressionParams(Map.of(
                    "$1", geo
            ));
            SqlExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);


            FeatureResultSet rs = dataSet.nearestByDialect(List.of("name"), expressionDialect, expressionParams, 111, 21, 1);

            List<Feature> res = new LinkedList<>();
            rs.forEachRemaining(f -> {
                res.add(f);
            });
            rs.close();
            Assert.assertEquals(res.size(), 0);
        }
    }
}
