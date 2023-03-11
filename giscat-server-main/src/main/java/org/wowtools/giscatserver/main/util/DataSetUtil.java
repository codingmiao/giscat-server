/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.OtherException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscatserver.dataset.api.DataSet;
import org.wowtools.giscatserver.dataset.api.ExpressionDialect;
import org.wowtools.giscatserver.dataset.api.FeatureResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据集工具类
 *
 * @author liuyu
 * @date 2023/3/3
 */
public class DataSetUtil {

    /**
     * 将字符串形式的表达式解析为json array对象
     *
     * @param expressionStr 表达式字符串
     * @return ArrayList
     */
    public static ArrayList toJsonArray(@Nullable String expressionStr) {
        if (null == expressionStr) {
            return null;
        }
        ArrayList expressionArray;
        try {
            expressionArray = Constant.jsonMapper.readValue(expressionStr, ArrayList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("解析json异常 " + expressionStr, e);
        }
        return expressionArray;
    }

    /**
     * 把两个json array的表达式以and合并成一个，并转换为方言表达式
     *
     * @param e1      e1
     * @param e2      e2
     * @param dataSet dataSet
     * @return ExpressionDialect
     */
    public static ExpressionDialect mergeExpression(@NotNull DataSet dataSet, ArrayList e1, ArrayList e2) {
        ArrayList expression1 = new ArrayList(3);
        expression1.add("all");
        expression1.add(e1);
        expression1.add(e2);
        Expression<Boolean> e = Expression.newInstance(expression1);
        return dataSet.buildExpressionDialect(e);
    }

    /**
     * 把3个json array的表达式以and合并成一个，并转换为方言表达式
     *
     * @param e1      e1
     * @param e2      e2
     * @param e3      e3
     * @param dataSet dataSet
     * @return ExpressionDialect
     */
    public static ExpressionDialect mergeExpression(@NotNull DataSet dataSet, ArrayList e1, ArrayList e2, ArrayList e3) {
        ArrayList expression1 = new ArrayList(3);
        expression1.add("all");
        expression1.add(e1);
        expression1.add(e2);
        expression1.add(e3);
        Expression<Boolean> e = Expression.newInstance(expression1);
        return dataSet.buildExpressionDialect(e);
    }

    /**
     * 把多个json array的表达式以and合并成一个，并转换为方言表达式
     *
     * @param es      es
     * @param dataSet dataSet
     * @return ExpressionDialect
     */
    public static ExpressionDialect mergeExpression(@NotNull DataSet dataSet, ArrayList @NotNull ... es) {
        ArrayList expression1 = new ArrayList(1 + es.length);
        expression1.add("all");
        expression1.addAll(Arrays.asList(es));
        Expression<Boolean> e = Expression.newInstance(expression1);
        return dataSet.buildExpressionDialect(e);
    }

    /**
     * 条件查询并返回list
     *
     * @param dataSet          数据集
     * @param propertyNames    查询的要素要返回哪些字段
     * @param expression       查询条件表达式
     * @param expressionParams 查询参数
     * @return features
     */
    public static @NotNull List<Feature> queryListByExpression(@NotNull DataSet dataSet, List<String> propertyNames, Expression<Boolean> expression, java.util.Map<String, Object> expressionParams) {
        ExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);
        ExpressionParams expressionParamsObj = new ExpressionParams(expressionParams);
        LinkedList<Feature> res = new LinkedList<>();
        try (FeatureResultSet frs = dataSet.queryByDialect(propertyNames, expressionDialect, expressionParamsObj)) {
            frs.forEachRemaining(res::add);
        } catch (Exception e) {
            throw new OtherException("queryListByExpression error", e);
        }
        return res;
    }

    /**
     * 最邻近查询并返回list
     *
     * @param propertyNames    查询的要素要返回哪些字段
     * @param expression       查询条件表达式
     * @param expressionParams 查询参数
     * @param x                x
     * @param y                y
     * @param n                最多返回几条数据
     * @return features
     */
    public static @NotNull List<Feature> nearest(@NotNull DataSet dataSet, List<String> propertyNames, Expression<Boolean> expression, java.util.Map<String, Object> expressionParams, double x, double y, int n) {
        ExpressionDialect expressionDialect = dataSet.buildExpressionDialect(expression);
        ExpressionParams expressionParamsObj = new ExpressionParams(expressionParams);
        LinkedList<Feature> res = new LinkedList<>();
        try (FeatureResultSet frs = dataSet.nearestByDialect(propertyNames, expressionDialect, expressionParamsObj, x, y, n)) {
            frs.forEachRemaining(res::add);
        } catch (Exception e) {
            throw new OtherException("queryListByExpression error", e);
        }
        return res;
    }
}
