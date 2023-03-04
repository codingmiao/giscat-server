/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.main.structure;


import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;

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

    private final MapLayer[] mapLayers;

    public Map(Layer[] layers, String[] layerNames) {
        if (layerNames.length != layers.length) {
            throw new ConfigException("图层数和图层名称不匹配");
        }
        mapLayers = new MapLayer[layerNames.length];
        for (int i = 0; i < layerNames.length; i++) {
            mapLayers[i] = new MapLayer(layerNames[i], layers[i]);
        }
    }

    public MapLayer[] getMapLayers() {
        return mapLayers;
    }
}
