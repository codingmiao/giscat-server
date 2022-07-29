/*****************************************************************
 *  Copyright (c) 2022- "giscat by 刘雨 (https://github.com/codingmiao/giscat)"
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.wowtools.giscatserver.dataset.api;


import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;
import org.wowtools.giscatserver.dataconnect.api.FeatureResultSet;

import java.util.Map;

/**
 * 数据集。数据集是数据连接对象中的一个存储单元，例如关系型数据库中的一张表
 *
 * @param <DC> 数据集所需的数据库连接
 * @param <ED> 数据集方言(如果有)
 */
public abstract class DatSet<DC extends DataConnect, ED extends ExpressionDialect> {
    /**
     * key-value格式的参数配置
     */
    protected final Map<String, Object> config;

    protected DC dataConnect;

    public DatSet(Map<String, Object> config) {
        this.config = config;
    }

    /**
     * 是否支持方言表达式查询
     *
     * @return true/false
     */
    public abstract boolean isSupportDialect();


    /**
     * 将表达式转为方言。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param expression 表达式
     * @return 方言
     */
    public abstract ExpressionDialect buildExpressionDialect(Expression expression);

    /**
     * 用方言进行条件查询。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param expressionDialect 方言
     * @return FeatureResultSet
     */
    public abstract FeatureResultSet queryByDialect(ExpressionDialect expressionDialect);

    /**
     * 用表达式进行条件查询。一般地，方言查询会比表达式查询性能更高，所以建议尽可能使用queryByDialect方法
     *
     * @param expression 表达式
     * @return FeatureResultSet
     */
    public abstract FeatureResultSet queryByExpression(Expression expression);


    /**
     * 用方言进行最邻近查询。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param expressionDialect 方言
     * @param x                 x
     * @param y                 y
     * @param n                 最多返回几条数据
     * @return FeatureResultSet
     */
    public abstract FeatureResultSet nearestByDialect(ExpressionDialect expressionDialect, double x, double y, int n);

    /**
     * 用表达式进行最邻近查询。一般地，方言查询会比表达式查询性能更高，所以建议尽可能使用nearestByDialect方法
     *
     * @param expression 表达式
     * @param x          x
     * @param y          y
     * @param n          最多返回几条数据
     * @return FeatureResultSet
     */
    public abstract FeatureResultSet nearestByExpression(Expression expression, double x, double y, int n);

}
