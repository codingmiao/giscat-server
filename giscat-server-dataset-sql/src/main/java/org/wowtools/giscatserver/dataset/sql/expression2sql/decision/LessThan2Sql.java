/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.dataset.sql.expression2sql.decision;

import org.jetbrains.annotations.NotNull;
import org.wowtools.giscat.vector.mbexpression.decision.LessThan;

/**
 * @author liuyu
 * @date 2023/2/13
 */
public class LessThan2Sql extends Compare2Sql<LessThan> {
    @Override
    protected @NotNull String getSymbol() {
        return "<";
    }
}
