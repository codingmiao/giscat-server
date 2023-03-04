/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataconnect.sql;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ExternalResourceException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
