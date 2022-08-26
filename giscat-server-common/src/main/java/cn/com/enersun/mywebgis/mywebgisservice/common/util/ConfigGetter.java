package cn.com.enersun.mywebgis.mywebgisservice.common.util;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;

import java.util.Map;

/**
 * 配置信息(map)获取工具
 *
 * @author liuyu
 * @date 2022/8/18
 */
public class ConfigGetter {
    public static <T> T getPropertyNotNull(Map<String, Object> config, String key) throws ConfigException {
        Object o = config.get(key);
        if (null == o) {
            throw new ConfigException("配置 " + key + " 不能为空");
        }
        try {
            T value = (T) o;
            return value;
        } catch (Exception e) {
            throw new ConfigException("配置 " + key + " 的值类型与预期不符");
        }
    }
}
