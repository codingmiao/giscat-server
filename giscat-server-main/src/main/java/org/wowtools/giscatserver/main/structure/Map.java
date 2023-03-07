/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.main.structure;


/**
 * 地图
 *
 * @author liuyu
 * @date 2023/2/24
 */
public class Map {

    public static final class MapLayer {
        private final String name;
        private final Layer layer;

        public MapLayer(String name, Layer layer) {
            this.name = name;
            this.layer = layer;
        }

        public String getName() {
            return name;
        }

        public Layer getLayer() {
            return layer;
        }
    }

    private final String id;
    private final MapLayer[] mapLayers;

    public Map(String id, MapLayer[] mapLayers) {
        this.id = id;
        this.mapLayers = mapLayers;
    }

    public MapLayer[] getMapLayers() {
        return mapLayers;
    }

    public String getId() {
        return id;
    }
}
