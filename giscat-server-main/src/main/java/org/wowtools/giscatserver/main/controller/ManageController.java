/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wowtools.giscatserver.main.ConfigManager;
import org.wowtools.giscatserver.main.service.VectorTileService;

/**
 * 系统管理
 *
 * @author liuyu
 * @date 2023/3/8
 */
@RestController()
@CrossOrigin
@RequestMapping("/Manage")
public class ManageController {
    /**
     * 重新加载数据库中的配置信息
     *
     * @return success
     */
    @RequestMapping("ReloadConfig")
    public @NotNull String reloadConfig() {
        ConfigManager.load();
        return "success";
    }

    /**
     * 清理矢量瓦片缓存
     *
     * @param id 地图id
     * @return success
     */
    @RequestMapping("ClearVectorTile/{id}")
    public @NotNull String clearVectorTile(@PathVariable String id) {
        VectorTileService service = ConfigManager.getVectorTileService(id);
        service.clearCache();
        return "success";
    }
}
