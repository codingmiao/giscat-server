/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.InputException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.ObjectUtils;
import org.wowtools.giscat.vector.pojo.FeatureCollection;
import org.wowtools.giscat.vector.pojo.converter.GeoJsonFeatureConverter;
import org.wowtools.giscat.vector.pojo.converter.ProtoFeatureConverter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author liuyu
 * @date 2023/3/7
 */
public class ServletUtil {
    private static final String bytesContentType = "application/octet-stream";
    private static final String jsonContentType = "application/json;charset=UTF-8";

    /**
     * 将bindParams参数转换为map对象
     *
     * @param bindParams bindParams
     * @return map
     */
    public static @Nullable Map<String, Object> bindParamsToMap(String bindParams) {
        if (ObjectUtils.isEmpty(bindParams)) {
            return null;
        } else {
            try {
                return Constant.jsonMapper.readValue(bindParams, java.util.Map.class);
            } catch (JsonProcessingException e) {
                throw new InputException("bindParams不是json", e);
            }
        }
    }

    /**
     * 将properties转换为list对象
     *
     * @param properties properties
     * @return list
     */
    public static @Nullable List<String> propertiesToList(@Nullable String properties) {
        if (ObjectUtils.isEmpty(properties)) {
            return null;
        } else {
            return List.of(properties.split(","));
        }
    }


    /**
     * 将矢量瓦片bytes写进HttpServletResponse
     *
     * @param bytes    bytes
     * @param timeOut  瓦片在多少毫秒后失效
     * @param response HttpServletResponse
     */
    public static void writeMvt(byte @NotNull [] bytes, long timeOut, @NotNull HttpServletResponse response) {
        response.setDateHeader("expires", RoughTimeUtil.getTimestamp() + timeOut);
        writeBytes(bytes, bytesContentType, response);
    }

    /**
     * 将FeatureCollection写进HttpServletResponse
     *
     * @param featureCollection featureCollection
     * @param f                 写入格式 json或pbf
     * @param response          HttpServletResponse
     */
    public static void writeFeatureCollection(@NotNull FeatureCollection featureCollection, String f, @NotNull HttpServletResponse response) {
        byte[] bytes;
        String contentType;
        if ("pbf".equals(f)) {
            bytes = ProtoFeatureConverter.featureCollection2Proto(featureCollection);
            contentType = bytesContentType;
        } else {
            String str = GeoJsonFeatureConverter.toGeoJson(featureCollection).toGeoJsonString();
            bytes = str.getBytes(StandardCharsets.UTF_8);
            contentType = jsonContentType;
        }
        writeBytes(bytes, contentType, response);
    }

    private static void writeBytes(byte @NotNull [] bytes, String contentType, @NotNull HttpServletResponse response) {
        response.setContentType(contentType);
        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes);
            os.flush();
        } catch (org.apache.catalina.connector.ClientAbortException e) {
            //客户端主动取消， 产生异常"你的主机中的软件中止了一个已建立的连接"，无需处理
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
