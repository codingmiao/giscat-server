/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.wowtools.dao.ConnectionPool;

/**
 * 常量
 *
 * @author liuyu
 * @date 2023/3/4
 */
public class Constant {
    public static final ObjectMapper jsonMapper = new ObjectMapper();

    public static final ConnectionPool giscatConfigConnectionPool = ConnectionPool.getOrInitInstance(Constant.class, "jdbccfg-giscat-config.json");
}
