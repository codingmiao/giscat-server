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
package org.wowtools.giscatserver.dataset.sql;

import org.reflections.Reflections;
import org.wowtools.giscat.vector.mbexpression.Expression;
import org.wowtools.giscat.vector.mbexpression.spatial.BboxIntersection;
import org.wowtools.giscat.vector.mbexpression.spatial.BboxIntersects;
import org.wowtools.giscat.vector.mbexpression.spatial.GeoIntersection;
import org.wowtools.giscat.vector.mbexpression.spatial.GeoIntersects;
import org.wowtools.giscatserver.dataset.sql.expression2sql.Expression2Sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Expression2Sql对象的管理工具
 *
 * @author liuyu
 * @date 2022/8/26
 */
public abstract class Expression2SqlManager {

    /**
     * shape字段占位符
     */
    public static final String ShapePlaceholder = "[shape]";

    private static void putImplByClass(Expression2Sql impl, Map<Class<? extends Expression>, Expression2Sql> impls) {
        if (null == impl) {
            return;
        }
        try {
            ParameterizedType superGenericSuperclass = (ParameterizedType) impl.getClass().getGenericSuperclass();
            Type type = superGenericSuperclass.getActualTypeArguments()[0];
            Class<? extends Expression> expressionClass = (Class<? extends Expression>) Class.forName(type.getTypeName());
            impls.put(expressionClass, impl);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Expression2Sql:" + impl.getClass(), e);
        }
    }

    /**
     * 通用的Expression2Sql实现类实例
     */
    private static final Map<Class<? extends Expression>, Expression2Sql> commonImpls;

    static {
        //扫描包下的所有实现类，初始化一个实例并放入impls对象以便按Expression获取Expression2Sql实例
        Reflections reflections = new Reflections("org.wowtools.giscatserver.dataset.sql.expression2sql");
        Set<Class<? extends Expression2Sql>> classList = reflections.getSubTypesOf(Expression2Sql.class);
        Map<Class<? extends Expression>, Expression2Sql> impls = new HashMap<>();
        for (Class<? extends Expression2Sql> aClass : classList) {
            if (Modifier.isAbstract(aClass.getModifiers())) {
                //抽象类不做初始化
                continue;
            }
            try {
                Constructor<? extends Expression2Sql> implConstructor = aClass.getDeclaredConstructor();
                implConstructor.setAccessible(true);
                Expression2Sql impl = implConstructor.newInstance();

                putImplByClass(impl, impls);
            } catch (Exception e) {
                throw new RuntimeException("Expression2Sql:" + aClass, e);
            } catch (Error e) {
                throw new RuntimeException("Expression2Sql:" + aClass, e);
            }
        }
        commonImpls = Map.copyOf(impls);
    }

    /**
     * 具体的Expression2Sql实现类实例，
     * 包含commonImpls中的所有实例及getBboxIntersection、getBboxIntersects、getGeoIntersection、getGeoIntersects、getExtends方法返回的实例
     */
    private final Map<Class<? extends Expression>, Expression2Sql> impls;

    {
        Map<Class<? extends Expression>, Expression2Sql> _impls = new HashMap<>();
        _impls.putAll(commonImpls);

        putImplByClass(getBboxIntersects(), _impls);
        putImplByClass(getGeoIntersects(), _impls);

        List<Expression2Sql> extendImpls = getExtends();
        if (null != extendImpls) {
            for (Expression2Sql extendImpl : extendImpls) {
                putImplByClass(extendImpl, _impls);
            }
        }

        impls = Map.copyOf(_impls);
    }

    public Expression2Sql getExpression2Sql(Expression expression) {
        Expression2Sql impl = impls.get(expression.getClass());
        if (null == impl) {
            throw new UnsupportedOperationException("不支持的expression " + expression);
        }
        return impl;
    }

    /**
     * 返回数据库具体的“BboxIntersects”空间查询实现
     *
     * @return BboxIntersects
     */
    protected abstract Expression2Sql<BboxIntersects> getBboxIntersects();

    /**
     * 返回数据库具体的“GeoIntersects”空间查询实现
     *
     * @return GeoIntersects
     */
    protected abstract Expression2Sql<GeoIntersects> getGeoIntersects();

    /**
     * 若数据库有一些自定义的Expression2Sql，可用此方法进行扩展并返回
     *
     * @return 若数据库有一些自定义的Expression2Sql，可用此方法进行扩展并返回一个list，若无，返回null
     */
    protected abstract List<Expression2Sql> getExtends();
}
