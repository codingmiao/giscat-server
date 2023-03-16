/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.api;


import org.wowtools.giscatserver.common.exception.ConfigException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;

import java.util.List;

/**
 * 数据集。数据集是数据连接对象中的一个存储单元，例如关系型数据库中的一张表
 *
 * @param <DC> 数据集所需的数据库连接
 * @param <ED> 数据集方言(如果有)
 */
@Slf4j
public abstract class DataSet<DC extends DataConnect, ED extends ExpressionDialect, CTX extends DataSetCtx> implements AutoCloseable {

    protected final String id;

    public DataSet(String id) {
        this.id = id;
    }

    /**
     * 将表达式转为方言。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param expression 表达式
     * @return 方言
     */
    public abstract ED buildExpressionDialect(@NotNull Expression<Boolean> expression);

    /**
     * 用方言进行条件查询。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param propertyNames     查询的要素要返回哪些字段
     * @param expressionDialect 方言
     * @param expressionParams  查询参数
     * @return FeatureResultSet
     */
    public abstract FeatureResultSet queryByDialect(@Nullable List<String> propertyNames, @Nullable ED expressionDialect, @Nullable ExpressionParams expressionParams);

    /**
     * 用方言进行最邻近查询。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param propertyNames     查询的要素要返回哪些字段
     * @param expressionDialect 方言
     * @param expressionParams  查询参数
     * @param x                 x
     * @param y                 y
     * @param n                 最多返回几条数据
     * @return FeatureResultSet
     */
    public abstract FeatureResultSet nearestByDialect(@Nullable List<String> propertyNames, @Nullable ED expressionDialect, @Nullable ExpressionParams expressionParams, double x, double y, int n);

    /**
     * 构造一个查询上下文
     *
     * @return CTX
     */
    protected abstract CTX createDatSetCtx();

    /**
     * 测试此数据集是否可用
     *
     * @throws ConfigException 不可用时抛出异常
     */
    public void test() throws ConfigException {
        try (FeatureResultSet frs = nearestByDialect(List.of(), null, null, 100, 20, 1)) {
            if (!frs.hasNext()) {
                log.warn("DataSet {} 中没有数据，无法确认其正确性", id);
            }
            frs.next();
        } catch (Exception e) {
            throw new ConfigException("test error", e);
        }

    }

    public String getId() {
        return id;
    }
}
