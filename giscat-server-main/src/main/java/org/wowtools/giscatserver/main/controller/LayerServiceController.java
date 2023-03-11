/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.*;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscat.vector.pojo.FeatureCollection;
import org.wowtools.giscatserver.main.ConfigManager;
import org.wowtools.giscatserver.main.service.LayerService;
import org.wowtools.giscatserver.main.util.ServletUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author liuyu
 * @date 2023/3/4
 */
@RestController()
@CrossOrigin
@RequestMapping("/Layer")
public class LayerServiceController {
    /**
     * 条件查询
     *
     * @param id         图层id
     * @param properties 返回的要素包含哪些属性,以半角逗号分隔
     * @param z          查询的层级，为空或为负则表示忽视图层rule中的zoom限制并取第0个rule
     * @param expression 表达式
     * @param bindParams 表达式绑定的参数
     * @param f          返回值格式，支持json和pbf，json返回geojson格式的FeatureCollection数据，pbf返回ProtoFeature格式的FeatureCollection数据
     * @param response   HttpServletResponse
     */
    @RequestMapping("{id}/Query")
    public void query(@PathVariable String id, @RequestParam(required = false) @Nullable String properties,
                      @RequestParam(required = false, defaultValue = "-1") byte z,
                      @RequestParam(required = false) @Nullable String expression, @RequestParam(required = false) @Nullable String bindParams,
                      @RequestParam(required = false, defaultValue = "json") @Nullable String f, @NotNull HttpServletResponse response) {
        LayerService service = ConfigManager.getLayerService(id);
        List<Feature> fs = service.query(ServletUtil.propertiesToList(properties), expression, ServletUtil.bindParamsToMap(bindParams), z);
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setFeatures(fs);
        ServletUtil.writeFeatureCollection(featureCollection, f, response);
    }

    /**
     * 最邻近查询
     *
     * @param id         图层id
     * @param properties 返回的要素包含哪些属性,以半角逗号分隔
     * @param z          查询的层级，为空或为负则表示忽视图层rule中的zoom限制并取第0个rule
     * @param x          x
     * @param y          y
     * @param n          每个图层各自最多返回几条数据
     * @param expression 表达式
     * @param bindParams 表达式绑定的参数
     * @param f          返回值格式，支持json和pbf，json返回geojson格式的FeatureCollection数据，pbf返回ProtoFeature格式的FeatureCollection数据
     * @param response   HttpServletResponse
     */
    @RequestMapping("{id}/Nearest")
    public void nearest(@PathVariable String id, @RequestParam(required = false) @Nullable String properties,
                        @RequestParam(required = false, defaultValue = "-1") byte z,
                        @RequestParam double x, @RequestParam double y, @RequestParam int n,
                        @RequestParam(required = false) @Nullable String expression, @RequestParam(required = false) @Nullable String bindParams,
                        @RequestParam(required = false, defaultValue = "json") @Nullable String f, @NotNull HttpServletResponse response) {
        LayerService service = ConfigManager.getLayerService(id);
        List<Feature> fs = service.nearest(ServletUtil.propertiesToList(properties), expression, ServletUtil.bindParamsToMap(bindParams),
                x, y, n, z);
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setFeatures(fs);
        ServletUtil.writeFeatureCollection(featureCollection, f, response);
    }
}
