/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import org.wowtools.giscatserver.dataconnect.sql.SqlDataConnect;
import org.wowtools.giscatserver.dataset.api.DataSetCtx;
import org.wowtools.giscatserver.dataset.api.DataSetLoader;

import java.util.Map;

/**
 * SqlDataSetLoader
 *
 * @author liuyu
 * @date 2022/9/1
 */
public abstract class SqlDataSetLoader<CTX extends DataSetCtx, T extends SqlDataSet<CTX>> extends DataSetLoader<SqlDataConnect, SqlExpressionDialect, CTX, T> {
    @Override
    public abstract T load(String id, Map<String, Object> dataSetConfig, SqlDataConnect dataConnect) throws ConfigException;
}
