/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.service;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.OtherException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscat.vector.mvt.MvtBuilder;
import org.wowtools.giscat.vector.mvt.MvtLayer;
import org.wowtools.giscat.vector.util.analyse.Bbox;
import org.wowtools.giscatserver.dataset.api.ExpressionDialect;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;
import org.wowtools.giscatserver.main.structure.LayerDataRule;
import org.wowtools.giscatserver.main.structure.Map;
import org.wowtools.giscatserver.main.util.AsyncTaskUtil;
import org.wowtools.giscatserver.main.util.Constant;
import org.wowtools.giscatserver.main.util.DataSetUtil;
import org.wowtools.giscatserver.main.util.FileCache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 矢量瓦片服务
 *
 * @author liuyu
 * @date 2023/3/6
 */
@Slf4j
public class VectorTileService implements Closeable {
    // expression重写，增加范围查询  ["bboxIntersects", ["$xmin","$ymin","$xmax","$ymax"]]
    private static final @NotNull ArrayList bboxIntersectsExpression;

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
        private final ExpressionDialect ruleExpressionDialect;

        public VectorTileServiceRule(LayerDataRule layerDataRule, ExpressionDialect ruleExpressionDialect) {
            this.layerDataRule = layerDataRule;
            this.ruleExpressionDialect = ruleExpressionDialect;
        }
    }

    public static final class VectorTileServiceLayer {
        private final Map.MapLayer mapLayer;
        private final List<String> propertyNames;
        private final VectorTileServiceRule @NotNull [] vtsRules;

        public @Nullable VectorTileServiceRule getVtsRule(byte z) {
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

        public VectorTileServiceLayer(Map.@NotNull MapLayer mapLayer, List<String> propertyNames, int simplifyDistance) {
            this.mapLayer = mapLayer;
            this.propertyNames = propertyNames;
            this.simplifyDistance = simplifyDistance;

            LayerDataRule[] rules = mapLayer.getLayer().getLayerDataRules();
            vtsRules = new VectorTileServiceRule[rules.length];
            for (int i = 0; i < vtsRules.length; i++) {
                LayerDataRule rule = rules[i];
                ExpressionDialect ed;
                ArrayList ruleExpression;
                if (null != rule.getRuleExpression()) {
                    ed = DataSetUtil.mergeExpression(rule.getDataSet(), bboxIntersectsExpression, rule.getRuleExpression());
                } else {
                    ruleExpression = bboxIntersectsExpression;
                    Expression<Boolean> e = Expression.newInstance(ruleExpression);
                    ed = rule.getDataSet().buildExpressionDialect(e);
                }
                vtsRules[i] = new VectorTileServiceRule(rule, ed);
            }

        }
    }

    private final VectorTileServiceLayer[] vectorTileServiceLayers;


    private final @NotNull FileCache fileCache;
    private final long cacheTimeOut;

    public VectorTileService(@NotNull Map map, long cacheTimeOut, @NotNull VectorTileServiceLayer[] vectorTileServiceLayers) {
        this.vectorTileServiceLayers = vectorTileServiceLayers;
        this.cacheTimeOut = cacheTimeOut;
        String cacheDir = Constant.cacheBaseDir + "/VectorTileService/" + map.getId();
        new File(cacheDir).mkdirs();
        this.fileCache = new FileCache(cacheDir, null, cacheTimeOut);
    }

    public byte[] exportVectorTile(byte z, int x, int y, @Nullable String strExpression, java.util.@Nullable Map<String, Object> expressionParams) {
        StringBuilder sb = new StringBuilder(z);
        sb.append('/').append(x).append('/').append(y);
        ArrayList expression;
        if (null != strExpression) {
            try {
                expression = Constant.jsonMapper.readValue(strExpression, java.util.ArrayList.class);
            } catch (JsonProcessingException e) {
                throw new OtherException("反序列化参数异常", e);
            }
            sb.append(strExpression);
            if (null != expressionParams) {
                try {
                    sb.append(Constant.jsonMapper.writeValueAsString(expressionParams));
                } catch (JsonProcessingException e) {
                    throw new OtherException("序列化参数异常", e);
                }
            }
        } else {
            expression = null;
        }
        byte[] cacheKey = FileCache.string2Bytes(sb.toString());
        byte[] cache = fileCache.get(cacheKey);
        if (null != cache) {
            return cache;
        }

        MvtBuilder mvtBuilder = new MvtBuilder(z, x, y, Constant.geometryFactory);
        //把参数绑上bbox范围
        Bbox bbox = mvtBuilder.getBbox();
        if (null == expressionParams) {
            expressionParams = java.util.Map.of(
                    "$xmin", bbox.xmin,
                    "$ymin", bbox.ymin,
                    "$xmax", bbox.xmax,
                    "$ymax", bbox.ymax
            );
        } else {
            expressionParams.put("$xmin", bbox.xmin);
            expressionParams.put("$ymin", bbox.ymin);
            expressionParams.put("$xmax", bbox.xmax);
            expressionParams.put("$ymax", bbox.ymax);
        }
        final java.util.Map<String, Object> fExpressionParams = expressionParams;
        List<Runnable> tasks = new ArrayList<>(vectorTileServiceLayers.length);
        for (VectorTileServiceLayer vtsLayer : vectorTileServiceLayers) {
            VectorTileServiceRule vtsRule = vtsLayer.getVtsRule(z);
            if (null == vtsRule) {
                continue;
            }
            LayerDataRule layerDataRule = vtsRule.layerDataRule;
            tasks.add(() -> {
                ExpressionDialect expressionDialect;
                if (null == expression) {
                    expressionDialect = vtsRule.ruleExpressionDialect;
                } else {
                    if (null == vtsRule.layerDataRule.getRuleExpression()) {
                        expressionDialect = DataSetUtil.mergeExpression(layerDataRule.getDataSet(),
                                bboxIntersectsExpression, expression);
                    } else {
                        expressionDialect = DataSetUtil.mergeExpression(layerDataRule.getDataSet(),
                                bboxIntersectsExpression, vtsRule.layerDataRule.getRuleExpression(), expression);
                    }
                }

                FeatureResultSet frs = layerDataRule.getDataSet().queryByDialect(vtsLayer.propertyNames,
                        expressionDialect, new ExpressionParams(fExpressionParams));
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


    public long getCacheTimeOut() {
        return cacheTimeOut;
    }

    public void clearCache() {
        fileCache.clear();
    }

    @Override
    public void close() throws IOException {
        fileCache.close();
    }
}
