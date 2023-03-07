/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package cn.com.enersun.mywebgis.mywebgisservice.common.exception;

/**
 * 外部资源调用出错，例如配置的某个外部数据库连不上
 *
 * @author liuyu
 * @date 2022/8/18
 */
public class ExternalResourceException extends RuntimeException {

    public ExternalResourceException(String message) {
        super(message);
    }

    public ExternalResourceException(String message, Throwable cause) {
        super(message, cause);
    }

}
