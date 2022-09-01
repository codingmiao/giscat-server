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
import org.wowtools.giscat.vector.mbexpression.ExpressionParams;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;

import java.util.List;

/**
 * 数据集。数据集是数据连接对象中的一个存储单元，例如关系型数据库中的一张表
 *
 * @param <DC> 数据集所需的数据库连接
 * @param <ED> 数据集方言(如果有)
 */
public abstract class DatSet<DC extends DataConnect, ED extends ExpressionDialect> {


    /**
     * 将表达式转为方言。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param expression 表达式
     * @return 方言
     */
    public abstract ED buildExpressionDialect(Expression<Boolean> expression);

    /**
     * 用方言进行条件查询。如果此数据集不支持方言，则抛出UnsupportedOperationException
     *
     * @param propertyNames     查询的要素要返回哪些字段
     * @param expressionDialect 方言
     * @param expressionParams  查询参数
     * @return FeatureResultSet
     */
    public abstract FeatureResultSet queryByDialect(List<String> propertyNames, ED expressionDialect, ExpressionParams expressionParams);

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
    public abstract FeatureResultSet nearestByDialect(List<String> propertyNames, ED expressionDialect, ExpressionParams expressionParams, double x, double y, int n);


}
