/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.api;

/**
 * 查询上下文，为了方便一些非线程安全对象(如WKBReader)在查询中的复用，可以把这些对象放在DatSetCtx中
 *
 * @author liuyu
 * @date 2022/9/1
 */
public abstract class DataSetCtx {
}
