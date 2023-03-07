/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.controller;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.InputException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.wowtools.giscatserver.main.ConfigManager;
import org.wowtools.giscatserver.main.util.Constant;
import org.wowtools.giscatserver.main.util.ServletUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * @author liuyu
 * @date 2023/3/4
 */
@RestController()
@CrossOrigin
@RequestMapping("/Map")
public class MapServiceController {

    /**
     * 生成矢量瓦片
     *
     * @param id         地图id
     * @param z          z
     * @param x          x
     * @param y          y
     * @param expression 表达式
     * @param bindParams 表达式绑定的参数
     * @param response   HttpServletResponse
     */
    @RequestMapping("{id}/VectorTile/{z}/{x}/{y}")
    public void exportVectorTile(@PathVariable String id, @PathVariable byte z, @PathVariable int x, @PathVariable int y,
                                 @RequestParam(required = false) String expression, @RequestParam(required = false) String bindParams,
                                 HttpServletResponse response) {
        java.util.Map<String, Object> expressionParams;
        if (ObjectUtils.isEmpty(bindParams)) {
            expressionParams = null;
        } else {
            try {
                expressionParams = Constant.jsonMapper.readValue(bindParams, java.util.Map.class);
            } catch (JsonProcessingException e) {
                throw new InputException("bindParams不是json", e);
            }
        }

        byte[] bytes = ConfigManager.getVectorTileService(id).exportVectorTile(z, x, y, expression, expressionParams);
        ServletUtil.exportByte(bytes, response);
    }
}
