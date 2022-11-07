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

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ExternalResourceException;
import com.zaxxer.hikari.HikariDataSource;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 关系型数据库连接，内部有一个Hikari连接池，获取java.sql.Connection
 *
 * @author liuyu
 * @date 2022/8/18
 */
public class SqlDataConnect extends DataConnect<Connection> {
    private final DataSource dataSource;

    public SqlDataConnect(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new ExternalResourceException(e);
        }
    }

    @Override
    public void close() {
        if (dataSource instanceof AutoCloseable) {
            AutoCloseable ac = (AutoCloseable) dataSource;
            try {
                ac.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
