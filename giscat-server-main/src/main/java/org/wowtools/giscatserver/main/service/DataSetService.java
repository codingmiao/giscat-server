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
import org.wowtools.giscatserver.dataset.api.DataSet;
import org.wowtools.giscatserver.main.util.DataSetUtil;

import java.util.List;

/**
 * 数据集服务
 *
 * @author liuyu
 * @date 2023/2/24
 */
public class DataSetService {

    private final DataSet dataSet;

    public DataSetService(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * 条件查询
     *
     * @param propertyNames    查询的要素要返回哪些字段
     * @param strExpression    查询条件表达式
     * @param expressionParams 查询参数
     * @return features
     */
    public List<Feature> query(List<String> propertyNames, String strExpression, ExpressionParams expressionParams) {
        Expression<Boolean> expression = Expression.newInstance(strExpression);
        return DataSetUtil.queryListByExpression(dataSet, propertyNames, expression, expressionParams);
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
     * @return features
     */
    public List<Feature> nearest(List<String> propertyNames, String strExpression, ExpressionParams expressionParams, double x, double y, int n) {
        Expression<Boolean> expression = Expression.newInstance(strExpression);
        return DataSetUtil.nearest(dataSet, propertyNames, expression, expressionParams, x, y, n);
    }

}
