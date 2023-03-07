/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.service;

import lombok.extern.slf4j.Slf4j;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscat.vector.mvt.MvtBuilder;
import org.wowtools.giscat.vector.mvt.MvtLayer;
import org.wowtools.giscat.vector.mvt.MvtParser;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscat.vector.util.analyse.Bbox;
import org.wowtools.giscatserver.dataset.api.ExpressionDialect;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;
import org.wowtools.giscatserver.main.structure.LayerDataRule;
import org.wowtools.giscatserver.main.structure.Map;
import org.wowtools.giscatserver.main.util.AsyncTaskUtil;
import org.wowtools.giscatserver.main.util.Constant;
import org.wowtools.giscatserver.main.util.FileCache;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 矢量瓦片服务
 *
 * @author liuyu
 * @date 2023/3/6
 */
@Slf4j
public class VectorTileService {
    // expression重写，增加范围查询  ["bboxIntersects", ["$xmin","$ymin","$xmax","$ymax"]]
    private static final ArrayList bboxIntersectsExpression;

    static {
        ArrayList bboxExpression = new ArrayList<>(4);
        bboxExpression.add("$xmin");
        bboxExpression.add("$ymin");
        bboxExpression.add("$xmax");
        bboxExpression.add("$ymax");
        bboxIntersectsExpression = new ArrayList<>(2);
        bboxIntersectsExpression.add("bboxIntersects");
        bboxIntersectsExpression.add(bboxExpression);
    }


    private static final class VectorTileServiceRule {
        private final LayerDataRule layerDataRule;
        private final ExpressionDialect expressionDialect;

        public VectorTileServiceRule(LayerDataRule layerDataRule, ExpressionDialect expressionDialect) {
            this.layerDataRule = layerDataRule;
            this.expressionDialect = expressionDialect;
        }
    }

    public static final class VectorTileServiceLayer {
        private final Map.MapLayer mapLayer;
        private final List<String> propertyNames;
        private final VectorTileServiceRule[] vtsRules;

        public VectorTileServiceRule getVtsRule(byte z) {
            if (z < 0) {
                return vtsRules[0];
            }
            for (VectorTileServiceRule vtsRule : vtsRules) {
                if (vtsRule.layerDataRule.matching(z)) {
                    return vtsRule;
                }
            }
            return null;
        }

        private final int simplifyDistance;

        public VectorTileServiceLayer(Map.MapLayer mapLayer, List<String> propertyNames, int simplifyDistance) {
            this.mapLayer = mapLayer;
            this.propertyNames = propertyNames;
            this.simplifyDistance = simplifyDistance;

            LayerDataRule[] rules = mapLayer.getLayer().getLayerDataRules();
            vtsRules = new VectorTileServiceRule[rules.length];
            for (int i = 0; i < vtsRules.length; i++) {
                LayerDataRule rule = rules[i];
                ArrayList ruleExpression;
                if (null != rule.getRuleExpression()) {
                    ruleExpression = new ArrayList(3);
                    ruleExpression.add("all");
                    ruleExpression.add(bboxIntersectsExpression);
                    ruleExpression.add(rule.getRuleExpression());
                } else {
                    ruleExpression = bboxIntersectsExpression;
                }
                ExpressionDialect ed = rules[i].getDataSet().buildExpressionDialect(Expression.newInstance(ruleExpression));
                vtsRules[i] = new VectorTileServiceRule(rules[i], ed);
            }

        }
    }

    private final VectorTileServiceLayer[] vectorTileServiceLayers;


    private final FileCache fileCache;

    public VectorTileService(Map map, long cacheTimeOut, VectorTileServiceLayer[] vectorTileServiceLayers) {
        this.vectorTileServiceLayers = vectorTileServiceLayers;
        String cacheDir = Constant.cacheBaseDir + "/VectorTileService/" + map.getId();
        new File(cacheDir).mkdirs();
        this.fileCache = new FileCache(cacheDir, null, cacheTimeOut);
    }

    public byte[] exportVectorTile(byte z, int x, int y, String strExpression, java.util.Map<String, Object> expressionParams) {
        byte[] fullBytes = exportFullVectorTile(z, x, y);
        if (null == strExpression) {
            return fullBytes;
        }
        // 如果用户输入条件非空，从fullBytes解析过滤
        Expression<Boolean> expression = Expression.newInstance(strExpression);
        ExpressionParams expressionParamsObj = new ExpressionParams(expressionParams);
        MvtParser.MvtFeatureLayer[] mvtFeatureLayers = MvtParser.parse2Wgs84Coords(z, x, y, fullBytes, Constant.geometryFactory);
        MvtBuilder mvtBuilder = new MvtBuilder(z, x, y, Constant.geometryFactory);
        for (MvtParser.MvtFeatureLayer mvtFeatureLayer : mvtFeatureLayers) {
            LinkedList<Feature> features = new LinkedList<>();
            for (Feature feature : mvtFeatureLayer.getFeatures()) {
                if (expression.getValue(feature, expressionParamsObj)) {
                    features.add(feature);
                }
            }

            if (features.size() > 0) {
                MvtLayer layer = mvtBuilder.getOrCreateLayer(mvtFeatureLayer.getLayerName());
                for (Feature feature : features) {
                    layer.addClipedFeature(feature);
                }
            }

        }
        byte[] bytes = mvtBuilder.toBytes();
        return bytes;
    }

    // 不带用户输入的过滤条件，查询全量数据瓦片
    private byte[] exportFullVectorTile(byte z, int x, int y) {
        StringBuilder sb = new StringBuilder(z);
        sb.append('/').append(x).append('/').append(y);
        byte[] cacheKey = FileCache.string2Bytes(sb.toString());
        byte[] cache = fileCache.get(cacheKey);
        if (null != cache) {
            return cache;
        }

        MvtBuilder mvtBuilder = new MvtBuilder(z, x, y, Constant.geometryFactory);
        //把参数绑上bbox范围
        Bbox bbox = mvtBuilder.getBbox();
        java.util.Map<String, Object> expressionParams = java.util.Map.of(
                "$xmin", bbox.xmin,
                "$ymin", bbox.ymin,
                "$xmax", bbox.xmax,
                "$ymax", bbox.ymax
        );
        List<Runnable> tasks = new ArrayList<>(vectorTileServiceLayers.length);
        for (VectorTileServiceLayer vtsLayer : vectorTileServiceLayers) {
            VectorTileServiceRule vtsRule = vtsLayer.getVtsRule(z);
            if (null == vtsRule) {
                continue;
            }
            LayerDataRule layerDataRule = vtsRule.layerDataRule;
            tasks.add(() -> {
                FeatureResultSet frs = layerDataRule.getDataSet().queryByDialect(vtsLayer.propertyNames, vtsRule.expressionDialect, new ExpressionParams(expressionParams));
                try {
                    if (frs.hasNext()) {
                        MvtLayer layer;
                        synchronized (mvtBuilder) {
                            layer = mvtBuilder.getOrCreateLayer(vtsLayer.mapLayer.getName(), vtsLayer.simplifyDistance);
                        }
                        do {
                            layer.addFeature(frs.next());
                        } while (frs.hasNext());
                    }
                } finally {
                    try {
                        frs.close();
                    } catch (Exception e) {
                        log.warn("关闭FeatureResultSet出错，可能产生对象泄露 DataSet: {}", layerDataRule.getDataSet().getId(), e);
                    }
                }
            });

        }
        AsyncTaskUtil.executeAsyncTasks(tasks);
        byte[] bytes = mvtBuilder.toBytes();
        fileCache.put(cacheKey, bytes);
        return bytes;
    }
}
