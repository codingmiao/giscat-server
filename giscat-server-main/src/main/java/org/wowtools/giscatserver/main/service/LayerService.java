/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.main.service;

import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscatserver.main.structure.Layer;
import org.wowtools.giscatserver.main.structure.LayerDataRule;
import org.wowtools.giscatserver.main.util.DataSetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 图层服务
 *
 * @author liuyu
 * @date 2023/2/24
 */
public class LayerService {

    private final Layer layer;

    public LayerService(Layer layer) {
        this.layer = layer;
    }

    /**
     * 条件查询
     *
     * @param propertyNames    查询的要素要返回哪些字段
     * @param strExpression    查询条件表达式
     * @param expressionParams 查询参数
     * @param zoom             层级。如果为负，则表示忽略层级，此时若layer中包含多个rule，则取首个rule并忽略其level
     * @return features 若为null则表示未找到对应的rule
     */
    public List<Feature> query(List<String> propertyNames, String strExpression, ExpressionParams expressionParams, byte zoom) {
        ArrayList expression = DataSetUtil.toJsonArray(strExpression);
        LayerDataRule rule = layer.getRule(zoom);
        if (null == rule) {
            return null;
        }
        Expression<Boolean> layerExpression = Layer.mergeLayerExpression(rule, expression);
        return DataSetUtil.queryListByExpression(rule.getDataSet(), propertyNames, layerExpression, expressionParams);
    }


    /**
     * 最邻近查询
     *
     * @param propertyNames    查询的要素要返回哪些字段
     * @param strExpression    查询条件表达式
     * @param expressionParams 查询参数
     * @param x                x
     * @param y                y
     * @param n                最多返回几条数据
     * @param zoom             层级。如或为负，则表示忽略层级，此时若layer中包含多个rule，则取首个rule并忽略其level
     * @return features 若为null则表示未找到对应的rule
     */
    public List<Feature> nearest(List<String> propertyNames, String strExpression, ExpressionParams expressionParams, double x, double y, int n, byte zoom) {
        ArrayList expression = DataSetUtil.toJsonArray(strExpression);
        LayerDataRule rule = layer.getRule(zoom);
        if (null == rule) {
            return null;
        }
        Expression<Boolean> layerExpression = Layer.mergeLayerExpression(rule, expression);
        return DataSetUtil.nearest(rule.getDataSet(), propertyNames, layerExpression, expressionParams, x, y, n);
    }


}
