/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.api;

import org.wowtools.giscatserver.common.exception.ConfigException;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;

import java.util.Map;

/**
 * 数据集加载器，与DatSet成对编写，用于将配置表中的配置信息加载为DatSet对象
 *
 * @param <T>   加载器加载的数据集类型
 * @param <DC>  数据集依赖的数据连接
 * @param <ED>  数据集的查询方言
 * @param <CTX> 数据集的查询上下文
 */
public abstract class DataSetLoader<DC extends DataConnect, ED extends ExpressionDialect, CTX extends DataSetCtx, T extends DataSet<DC, ED, CTX>> {
    public abstract T load(String id, Map<String, Object> dataSetConfig, DC dataConnect) throws ConfigException;
}
