/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.structure;

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

    private final ArrayList ruleExpression;

    public LayerDataRule(byte minZoom, byte maxZoom, DataSet dataSet, ArrayList ruleExpression) {
        if (maxZoom < 0) {
            minZoom = Byte.MAX_VALUE;
        }
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        this.dataSet = dataSet;
        this.ruleExpression = ruleExpression;
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

    public ArrayList getRuleExpression() {
        return ruleExpression;
    }
}
