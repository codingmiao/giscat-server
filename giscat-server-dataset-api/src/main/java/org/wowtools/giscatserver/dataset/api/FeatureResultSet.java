/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
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
