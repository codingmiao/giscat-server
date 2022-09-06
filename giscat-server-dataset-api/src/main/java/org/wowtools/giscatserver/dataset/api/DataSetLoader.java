/*****************************************************************
 *  Copyright (c) 2022- "giscat by 刘雨 (https://github.com/codingmiao/giscat)"
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.wowtools.giscatserver.dataset.api;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;

import java.util.Map;

/**
 * 数据集加载器，与DatSet成对编写，用于将配置表中的配置信息加载为DatSet对象
 *
 * @param <T> 加载器加载的数据集类型
 * @param <DC> 数据集依赖的数据连接
 * @param <ED> 数据集的查询方言
 * @param <CTX> 数据集的查询上下文
 */
public abstract class DataSetLoader<DC extends DataConnect, ED extends ExpressionDialect, CTX extends DataSetCtx, T extends DataSet<DC, ED, CTX>> {
    public abstract T load(Map<String, Object> dataSetConfig, DC dataConnect) throws ConfigException;
}
