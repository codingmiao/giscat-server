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
package org.wowtools.giscatserver.dataconnect.sql;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import cn.com.enersun.mywebgis.mywebgisservice.common.util.ConfigGetter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.wowtools.giscatserver.dataconnect.api.DataConnectLoader;

import java.util.Map;
import java.util.Properties;

/**
 * SqlDataConnect加载器
 *
 * @author liuyu
 * @date 2022/8/18
 */
public class SqlDataConnectLoader extends DataConnectLoader<SqlDataConnect> {

    @Override
    public SqlDataConnect load(Map<String, Object> dataConnectConfig) throws ConfigException {
        Map<String, Object> cpConfig = ConfigGetter.getPropertyNotNull(dataConnectConfig, "hikari");
        HikariDataSource dataSource;
        try {
            Properties properties = new Properties(cpConfig.size());
            cpConfig.forEach((k, v) -> properties.setProperty(k, String.valueOf(v)));
            HikariConfig configuration = new HikariConfig();
            dataSource = new HikariDataSource(configuration);
        } catch (Exception e) {
            throw new ConfigException("hikari配置错误", e);
        }
        return new SqlDataConnect(dataSource);
    }
}
