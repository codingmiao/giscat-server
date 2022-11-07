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

import org.wowtools.giscatserver.dataset.api.ExpressionDialect;

import java.util.LinkedList;
import java.util.List;

/**
 * sql方言，将表达式转换为sql语句
 *
 * @author liuyu
 * @date 2022/8/23
 */
public class SqlExpressionDialect extends ExpressionDialect {

    /**
     * where部分的sql字符串，形如 a=? and b=?
     */
    private final String wherePart;

    /**
     * 第index-1个绑定参数存放的是哪个变量,形如[$x,$y]
     */
    private final String[] paramNames;


    /**
     * @param wherePart where部分的sql字符串(以$符号表示参数绑定)，形如 a=$x and b=$y
     */
    public SqlExpressionDialect(String wherePart) {
        /* 替换$符参数绑定 */
        int size = wherePart.length();
        StringBuilder newWherePart = new StringBuilder();
        List<String> paramNameList = new LinkedList<>();
        boolean inStrParam = false;//当前位置是否在一个字符参数内，若是，不作解析
        boolean in$ = false;//当前位置是否在$参数内，若是，读取字符拼接参数
        StringBuilder paramName = null;
        for (int i = 0; i < size; i++) {
            char c = wherePart.charAt(i);
            newWherePart.append(c);
            if (c == '\'') {
                inStrParam = !inStrParam;
                continue;
            }
            if (inStrParam) {
                continue;
            }
            if (c == '$') {
                in$ = !in$;
                paramName = new StringBuilder("$");
                continue;
            }
            if (in$) {
                if (c == ' ' || c == '\t' || c == ',' || c == ')') {
                    in$ = false;
                    //找一个$参数结束，保存结果
                    String name = paramName.toString();
                    paramNameList.add(name);
                    newWherePart.delete(newWherePart.length() - name.length() - 1, newWherePart.length());
                    newWherePart.append("?").append(c);
                    paramName = null;
                } else {
                    paramName.append(c);
                }
            }
        }
        if (null != paramName) {
            String name = paramName.toString();
            paramNameList.add(name);
            newWherePart.delete(newWherePart.length() - name.length(), newWherePart.length());
            newWherePart.append("?");
        }
        this.wherePart = newWherePart.toString();
        paramNames = new String[paramNameList.size()];
        paramNameList.toArray(paramNames);
    }

    /**
     * where部分的sql字符串，形如 a=? and b=?
     */
    public String getWherePart() {
        return wherePart;
    }

    /**
     * 第index-1个绑定参数存放的是哪个变量,形如[$x,$y]
     */
    public String[] getParamNames() {
        return paramNames;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(wherePart);
        sb.append("\t paramNames: ");
        for (String paramName : paramNames) {
            sb.append("{").append(paramName).append("}, ");
        }
        return sb.toString();
    }
}
