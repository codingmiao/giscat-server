/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package cn.com.enersun.mywebgis.mywebgisservice.common.exception;

/**
 * 其它异常
 *
 * @author liuyu
 * @date 2023/3/3
 */
public class OtherException extends RuntimeException {

    public OtherException(String message) {
        super(message);
    }

    public OtherException(String message, Throwable cause) {
        super(message, cause);
    }
}
