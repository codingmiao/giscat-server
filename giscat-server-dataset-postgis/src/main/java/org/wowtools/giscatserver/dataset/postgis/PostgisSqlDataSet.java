package org.wowtools.giscatserver.dataset.postgis;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.postgresql.util.PGobject;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscatserver.dataconnect.sql.SqlDataConnect;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;
import org.wowtools.giscatserver.dataset.sql.Expression2SqlManager;
import org.wowtools.giscatserver.dataset.sql.SqlDataSet;
import org.wowtools.giscatserver.dataset.sql.SqlExpressionDialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * postgis关系型数据库数据集
 *
 * @author liuyu
 * @date 2022/9/1
 */
public class PostgisSqlDataSet extends SqlDataSet<PostgisDataSetCtx> {
    /**
     * @param dataConnect           数据集使用的数据连接
     * @param expression2SqlManager 转换sql的管理器
     * @param tableName             数据集对应表名，也可以是一个sql语句，传入sql语句时需要加括号和别名例如"(select a,b from xxx) t"
     * @param shapeName             空间数据字段名
     */
    public PostgisSqlDataSet(SqlDataConnect dataConnect, Expression2SqlManager expression2SqlManager, String tableName, String shapeName) {
        super(dataConnect, expression2SqlManager, tableName, shapeName);
    }

    @Override
    public FeatureResultSet nearestByDialect(List<String> propertyNames, SqlExpressionDialect expressionDialect, ExpressionParams expressionParams, double x, double y, int n) {
        return null;
    }

    @Override
    protected PostgisDataSetCtx createDatSetCtx() {
        return new PostgisDataSetCtx();
    }

    @Override
    protected Geometry readGeometry(ResultSet rs, int shapeIndex, PostgisDataSetCtx ctx) throws SQLException {
        PGobject obj = (PGobject) rs.getObject(shapeIndex);
        if (null == obj) {
            return null;
        }
        byte[] bytes = WKBReader.hexToBytes(obj.getValue());
        Geometry geo;
        try {
            geo = ctx.wkbReader.read(bytes);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return geo;
    }
}
