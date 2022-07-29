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
package org.wowtools.giscatserver.dataconnect.api;

import java.util.Map;

/**
 * 数据连接，数据连接是与数据存储目标的连接对象，例如连接到关系型数据库的Connection
 *
 * @param <T> 数据连接具体对象，例如关系型数据库的java.sql.Connection对象
 */
public abstract class DataConnect<T extends AutoCloseable> {
    /**
     * key-value格式的参数配置
     */
    protected final Map<String, Object> config;

    public DataConnect(Map<String, Object> config) {
        this.config = config;
    }

    /**
     * 获取实际的连接对象
     *
     * @return
     */
    public abstract T getConnection();

    /**
     * 关闭数据连接，在此做一些关闭操作，例如，数据连接的实现对应到一个数据库连接池，则需要显式地关闭连接池，以免连接池对象不被释放。
     * @throws Exception 关闭时抛出的任何可能的异常
     */
    public abstract void close() throws Exception;
}
