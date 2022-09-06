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
    public abstract T load(Map<String, Object> dataSetConfig, SqlDataConnect dataConnect) throws ConfigException;
}
