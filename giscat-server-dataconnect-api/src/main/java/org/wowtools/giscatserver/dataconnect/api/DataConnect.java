/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataconnect.api;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ExternalResourceException;

/**
 * 数据连接，数据连接是与数据存储目标的连接对象，例如连接到关系型数据库的Connection
 *
 * @param <T> 数据连接具体对象，例如关系型数据库的java.sql.Connection对象
 */
public abstract class DataConnect<T extends AutoCloseable> {


    /**
     * 获取实际的连接对象
     *
     * @return 实际的连接对象
     * @throws ExternalResourceException 当连接异常时抛出
     */
    public abstract T getConnection() throws ExternalResourceException;

    /**
     * 关闭数据连接，在此做一些关闭操作，例如，数据连接的实现对应到一个数据库连接池，则需要显式地关闭连接池，以免连接池对象不被释放。
     *
     * @throws Exception 关闭时抛出的任何可能的异常
     */
    public abstract void close() throws Exception;

    /**
     * 测试此数据连接是否可用
     */
    public void test() throws ConfigException {
        try {
            T conn = getConnection();
            conn.close();
        } catch (Exception e) {
            throw new ConfigException("test error", e);
        }
    }
}
