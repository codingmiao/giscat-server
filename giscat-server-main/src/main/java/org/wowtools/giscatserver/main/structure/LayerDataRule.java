/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscatserver.dataset.api.DataSet;

import java.util.ArrayList;


/**
 * 图层规则，在不同层级下，一个图层可能有不同的数据获取规则
 *
 * @author liuyu
 * @date 2023/3/3
 */
public class LayerDataRule {
    private final byte minZoom;
    private final byte maxZoom;
    private final DataSet dataSet;

    private final @Nullable ArrayList ruleExpression;

    private final @Nullable Expression<Boolean> ruleExpressionObj;

    private final byte orderNum;

    public LayerDataRule(byte minZoom, byte maxZoom, @NotNull DataSet dataSet, @Nullable ArrayList ruleExpression, byte orderNum) {
        if (maxZoom < 0) {
            maxZoom = Byte.MAX_VALUE;
        }
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        this.dataSet = dataSet;
        this.ruleExpression = ruleExpression;
        this.ruleExpressionObj = null == ruleExpression ? null : Expression.newInstance(ruleExpression);
        this.orderNum = orderNum;
    }

    /**
     * 输入的层级是否与此规则匹配
     *
     * @param zoom 输入层级
     * @return 是否匹配
     */
    public boolean matching(byte zoom) {
        return zoom >= minZoom && zoom <= maxZoom;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public @Nullable ArrayList getRuleExpression() {
        return ruleExpression;
    }

    public byte getMinZoom() {
        return minZoom;
    }

    public byte getMaxZoom() {
        return maxZoom;
    }

    public byte getOrderNum() {
        return orderNum;
    }

    public @Nullable Expression<Boolean> getRuleExpressionObj() {
        return ruleExpressionObj;
    }
}
