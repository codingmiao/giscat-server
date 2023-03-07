/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.wowtools.giscat.vector.mvt.MvtParser;
//import org.wowtools.giscat.vector.pojo.FeatureCollection;
//import org.wowtools.giscatserver.main.util.Constant;
//
//import java.util.List;
//import java.util.Map;

/**
 * @author liuyu
 * @date 2023/2/24
 */
@SpringBootApplication
public class StartUp {
    public static void main(String[] args) {
        ConfigManager.load();
//        FeatureCollection rs = ConfigManager.getMapService("test_map").nearest(List.of("id"), (String) null, null, 90d, 20d, 3, (byte) -1);
//        System.out.println(rs);
//        rs = ConfigManager.getMapService("test_map").query(List.of("id"), "[\"==\", [\"get\", \"id\"], \"$a\"]", Map.of("$a", 1), (byte) -1);
//        System.out.println(rs);
//        byte[] bytes = ConfigManager.getVectorTileService("test_map").exportVectorTile((byte) 6, 50, 26, null, null);
//        MvtParser.MvtFeatureLayer[] mvtFeatureLayers = MvtParser.parse2TileCoords(bytes, Constant.geometryFactory);
//        System.out.println(mvtFeatureLayers);
        SpringApplication.run(StartUp.class, args);

    }
}
