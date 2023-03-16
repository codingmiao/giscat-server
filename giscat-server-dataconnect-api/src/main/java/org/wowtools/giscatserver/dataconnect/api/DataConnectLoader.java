/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataconnect.api;

import org.wowtools.giscatserver.common.exception.ConfigException;

import java.util.Map;

/**
 * 数据连接加载器，与DataConnect成对编写，用于将配置表中的配置信息加载为DataConnect对象
 *
 * @param <T> 加载器加载的数据连接类型
 */
public abstract class DataConnectLoader<T extends DataConnect> {
    public abstract T load(String id, Map<String, Object> dataConnectConfig) throws ConfigException;
}
