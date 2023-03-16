/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.dataset.postgis;

import org.wowtools.giscatserver.common.exception.ConfigException;
import org.jetbrains.annotations.NotNull;
import org.wowtools.giscatserver.dataconnect.sql.SqlDataConnect;
import org.wowtools.giscatserver.dataset.api.DataSetLoader;
import org.wowtools.giscatserver.dataset.sql.SqlExpressionDialect;

import java.util.Map;

/**
 * @author liuyu
 * @date 2023/3/4
 */
public class PostgisDataSetLoader extends DataSetLoader<SqlDataConnect, SqlExpressionDialect, PostgisDataSetCtx, PostgisSqlDataSet> {
    @Override
    public @NotNull PostgisSqlDataSet load(String id, @NotNull Map<String, Object> dataSetConfig, SqlDataConnect dataConnect) throws ConfigException {
        return new PostgisSqlDataSet(id, dataConnect,
                new PostgisExpression2SqlManager(),
                (String) dataSetConfig.get("tableName"),
                (String) dataSetConfig.get("shapeColumn"));
    }
}
