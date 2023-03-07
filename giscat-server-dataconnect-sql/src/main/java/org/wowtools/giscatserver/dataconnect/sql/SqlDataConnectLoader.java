/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataconnect.sql;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
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
    public SqlDataConnect load(String id, Map<String, Object> cpConfig) throws ConfigException {
        HikariDataSource dataSource;
        Properties properties = new Properties(cpConfig.size());
        cpConfig.forEach((k, v) -> properties.setProperty(k, String.valueOf(v)));
        HikariConfig configuration = new HikariConfig(properties);
        dataSource = new HikariDataSource(configuration);
        return new SqlDataConnect(id, dataSource);
    }
}
