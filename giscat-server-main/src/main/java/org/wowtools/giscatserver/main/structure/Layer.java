/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.main.structure;

import org.wowtools.giscat.vector.mbexpression.Expression;

import java.util.ArrayList;

/**
 * 图层
 *
 * @author liuyu
 * @date 2023/2/24
 */
public class Layer {
    private final String id;
    private final LayerDataRule[] layerDataRules;

    public Layer(String id, LayerDataRule[] layerDataRules) {
        this.id = id;
        this.layerDataRules = layerDataRules;
    }

    /**
     * 获取输入条件匹配的zoom
     *
     * @param zoom zoom
     * @return LayerDataRule
     */
    public LayerDataRule getRule(byte zoom) {
        if (zoom < 0) {
            return layerDataRules[0];
        }
        for (LayerDataRule layerDataRule : layerDataRules) {
            if (layerDataRule.matching(zoom)) {
                return layerDataRule;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    /**
     * 将一个表达式与当前rule合并得到新表达式
     *
     * @param rule       rule
     * @param expression expression
     * @return expression
     */
    public static Expression<Boolean> mergeLayerExpression(LayerDataRule rule, ArrayList expression) {
        if (null != rule.getRuleExpression()) {
            if (null == expression) {
                return rule.getRuleExpressionObj();
            }
            ArrayList layerExpression = new ArrayList(3);
            layerExpression.add("all");
            layerExpression.add(expression);
            layerExpression.add(rule.getRuleExpression());
            return Expression.newInstance(layerExpression);
        } else {
            if (null == expression) {
                return null;
            }
            return Expression.newInstance(expression);
        }
    }


    public LayerDataRule[] getLayerDataRules() {
        return layerDataRules;
    }
}
