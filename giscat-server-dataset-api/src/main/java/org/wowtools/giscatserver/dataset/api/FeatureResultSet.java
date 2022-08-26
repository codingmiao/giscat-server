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

import org.wowtools.giscat.vector.pojo.Feature;

import java.util.Iterator;

/**
 * 要素查询结果集。
 * 注意，实现其close方法时，务必需要将关联对象关闭。例如一个关系型数据库查询出的FeatureResultSet,close方法中务必关闭查询相关的Connection、PreparedStatement等jdbc对象。
 *
 * @author liuyu
 * @date 2022/7/29
 */
public interface FeatureResultSet extends AutoCloseable, Iterator<Feature> {
}
