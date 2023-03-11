/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.GeometryFactory;
import org.wowtools.common.utils.ResourcesReader;
import org.wowtools.dao.ConnectionPool;

/**
 * 常量
 *
 * @author liuyu
 * @date 2023/3/4
 */
public class Constant {
    public static final ObjectMapper jsonMapper = new ObjectMapper();

    public static final GeometryFactory geometryFactory = new GeometryFactory();

    public static final ConnectionPool giscatConfigConnectionPool = ConnectionPool.getOrInitInstance(Constant.class, "jdbccfg-giscat-config.json");

    /**
     * 缓存文件根目录
     */
    public static final String cacheBaseDir;

    /**
     * 多线程查询时最多允许同时开几个线程，这些线程数是全局共用的，例如限制为32，查询A用掉了20，则在A释放之前，其它查询只有12个可用
     */
    public static final int threadPoolMaxNum;

    /**
     * RoughTimeUtil工具获取到的时间戳精度(毫秒)
     */
    public static final long roughTimePrecision;


    static {
        try {
            JSONObject config = new JSONObject(ResourcesReader.readStr(Constant.class, "config.json"));
            cacheBaseDir = config.getString("cacheBaseDir");
            threadPoolMaxNum = config.getInt("threadPoolMaxNum");
            roughTimePrecision = config.getLong("roughTimePrecision");
        } catch (Exception e) {
            throw new ConfigException("读取config.json异常", e);
        }
    }
}
