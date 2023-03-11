/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package cn.com.enersun.mywebgis.mywebgisservice.common.util;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 配置信息(map)获取工具
 *
 * @author liuyu
 * @date 2022/8/18
 */
public class ConfigGetter {
    public static <T> @NotNull T getPropertyNotNull(@NotNull Map<String, Object> config, String key) throws ConfigException {
        Object o = config.get(key);
        if (null == o) {
            throw new ConfigException("配置 " + key + " 不能为空");
        }
        try {
            return (T) o;
        } catch (Exception e) {
            throw new ConfigException("配置 " + key + " 的值类型与预期不符");
        }
    }
}
