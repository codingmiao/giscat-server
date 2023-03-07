/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql.expression2sql.decision;

import org.wowtools.giscat.vector.mbexpression.decision.Equal;

/**
 * @author liuyu
 * @date 2022/8/26
 */
public class Equal2Sql extends Compare2Sql<Equal> {

    @Override
    protected String getSymbol() {
        return "=";
    }
}
