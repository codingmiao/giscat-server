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
import org.wowtools.giscat.vector.pojo.FeatureCollection;
import org.wowtools.giscatserver.main.structure.Layer;
import org.wowtools.giscatserver.main.structure.LayerDataRule;
import org.wowtools.giscatserver.main.util.DataSetUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 地图服务
 *
 * @author liuyu
 * @date 2023/2/24
 */
public class MapService {

    private final org.wowtools.giscatserver.main.structure.Map map;

    public MapService(org.wowtools.giscatserver.main.structure.Map map) {
        this.map = map;
    }

    private static abstract class FeatureCollectionBuilder {
        protected abstract List<Feature> get(LayerDataRule rule, List<String> propertyNames, Expression<Boolean> layerExpression, ExpressionParams expressionParams);

        FeatureCollection build(org.wowtools.giscatserver.main.structure.Map map, List<String> propertyNames, String strExpression, ExpressionParams expressionParams, byte zoom) {
            org.wowtools.giscatserver.main.structure.Map.MapLayer[] mapLayers = map.getMapLayers();
            ArrayList expression = DataSetUtil.toJsonArray(strExpression);
            ArrayList<String> layerNames = new ArrayList<>(mapLayers.length);
            ArrayList<Integer> featureIndexes = new ArrayList<>(mapLayers.length);
            List<Feature> features = new LinkedList<>();
            int index = -1;
            for (org.wowtools.giscatserver.main.structure.Map.MapLayer mapLayer : mapLayers) {
                LayerDataRule rule = mapLayer.getLayer().getRule(zoom);
                if (null == rule) {
                    continue;
                }
                Expression<Boolean> layerExpression = Layer.mergeLayerExpression(rule, expression);
                List<Feature> layerFeatures = get(rule, propertyNames, layerExpression, expressionParams);

                layerNames.add(mapLayer.getName());

                index += layerFeatures.size();
                featureIndexes.add(index);

                features.addAll(layerFeatures);
            }
            FeatureCollection featureCollection = new FeatureCollection();
            featureCollection.setFeatures(features);
            featureCollection.setHeaders(java.util.Map.of(
                    "layerNames", layerNames,
                    "featureIndexes", featureIndexes
            ));
            return featureCollection;
        }
    }

    private static final class QueryFeatureCollectionBuilder extends FeatureCollectionBuilder {
        @Override
        protected List<Feature> get(LayerDataRule rule, List<String> propertyNames, Expression<Boolean> layerExpression, ExpressionParams expressionParams) {
            return DataSetUtil.queryListByExpression(rule.getDataSet(), propertyNames, layerExpression, expressionParams);
        }
    }

    private static final class NearestFeatureCollectionBuilder extends FeatureCollectionBuilder {
        private final double x;
        private final double y;
        private final int n;

        public NearestFeatureCollectionBuilder(double x, double y, int n) {
            this.x = x;
            this.y = y;
            this.n = n;
        }

        @Override
        protected List<Feature> get(LayerDataRule rule, List<String> propertyNames, Expression<Boolean> layerExpression, ExpressionParams expressionParams) {
            return DataSetUtil.nearest(rule.getDataSet(), propertyNames, layerExpression, expressionParams, x, y, n);
        }
    }

    /**
     * 条件查询
     *
     * @param propertyNames    查询的要素要返回哪些字段
     * @param strExpression    查询条件表达式
     * @param expressionParams 查询参数
     * @param zoom             层级。如果为负，则表示忽略层级，此时若map中的layer中包含多个rule，则此layer取首个rule并忽略其level
     * @return FeatureCollection features为查询结果，headers中layers属性描述了范围。
     * 如 {layerNames:['a','b'],featureIndexes:[5,13]} 表示图层a的结果为第0-5个要素，图层b的结果为第6-13个要素
     */
    public FeatureCollection query(List<String> propertyNames, String strExpression, ExpressionParams expressionParams, byte zoom) {
        return new QueryFeatureCollectionBuilder().build(map, propertyNames, strExpression, expressionParams, zoom);
    }

    /**
     * 最邻近查询
     *
     * @param propertyNames    查询的要素要返回哪些字段
     * @param strExpression    查询条件表达式
     * @param expressionParams 查询参数
     * @param x                x
     * @param y                y
     * @param n                每个图层各自最多返回几条数据
     * @param zoom             层级。如果为负，则表示忽略层级，此时若map中的layer中包含多个rule，则此layer取首个rule并忽略其level
     * @return FeatureCollection features为查询结果，headers中layers属性描述了范围。
     * 如 {layerNames:['a','b'],featureIndexes:[5,13]} 表示图层a的结果为第0-5个要素，图层b的结果为第6-13个要素
     */
    public FeatureCollection nearest(List<String> propertyNames, String strExpression, ExpressionParams expressionParams, double x, double y, int n, byte zoom) {
        return new NearestFeatureCollectionBuilder(x, y, n).build(map, propertyNames, strExpression, expressionParams, zoom);
    }
}
