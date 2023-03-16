/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main;

import org.wowtools.giscatserver.common.exception.ConfigException;
import org.wowtools.giscatserver.common.exception.InputException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.cglib.core.internal.Function;
import org.wowtools.common.utils.ResourcesReader;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;
import org.wowtools.giscatserver.dataconnect.api.DataConnectLoader;
import org.wowtools.giscatserver.dataset.api.DataSet;
import org.wowtools.giscatserver.dataset.api.DataSetLoader;
import org.wowtools.giscatserver.main.service.DataSetService;
import org.wowtools.giscatserver.main.service.LayerService;
import org.wowtools.giscatserver.main.service.MapService;
import org.wowtools.giscatserver.main.service.VectorTileService;
import org.wowtools.giscatserver.main.structure.Layer;
import org.wowtools.giscatserver.main.structure.LayerDataRule;
import org.wowtools.giscatserver.main.structure.Map;
import org.wowtools.giscatserver.main.util.Constant;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;


/**
 * 配置信息的读取与管理
 *
 * @author liuyu
 * @date 2023/3/4
 */
@Slf4j
public class ConfigManager {
    private static java.util.Map<String, DataConnectLoader> dataConnectLoaders;

    private static java.util.Map<String, DataSetLoader> dataSetLoaders;


    private static java.util.Map<String, DataConnect> dataConnects;

    private static java.util.Map<String, DataSet> dataSets;

    private static java.util.Map<String, LayerDataRule[]> layerDataRules;

    private static java.util.Map<String, Layer> layers;

    private static java.util.Map<String, Map.MapLayer[]> layerInMaps;
    private static java.util.Map<String, Map> maps;

    private static java.util.Map<String, DataSetService> dataSetServices;

    private static java.util.Map<String, LayerService> layerServices;

    private static java.util.Map<String, MapService> mapServices;

    private static java.util.Map<String, VectorTileService> vectorTileServices;

    /**
     * 加载配置信息为对象实例
     */
    public static synchronized void load() {
        log.info("ConfigManager load start");
        loadPlugIns();
        loadDataConnect();
        loadDataSet();
        loadLayerDataRule();
        loadLayer();
        loadLayerInMaps();
        loadMap();
        loadDataSetService();
        loadLayerService();
        loadMapSetService();
        loadVectorTileService();
        log.info("ConfigManager load success");
    }

    private static void loadPlugIns() {
        //加载DataConnectLoader、DataSetLoader插件
        URLClassLoader plugInClassLoader;
        try {
            HashSet<String> jarPaths = new HashSet<>();
            //读addons目录下的插件
            {
                String addonsRoot = ResourcesReader.getRootPath(ConfigManager.class) + "/addons";
                File root = new File(addonsRoot);
                if (root.exists()) {
                    for (File file : root.listFiles()) {
                        String fileName = file.getName();
                        if (fileName.startsWith(".jar", fileName.length() - 4)) {
                            jarPaths.add(file.getAbsolutePath());
                        }
                    }
                }
            }

            Function<String, Boolean> loader = jsonPath -> {
                String strDataConnectLoaderImpls = ResourcesReader.readStr(ConfigManager.class, jsonPath);
                JSONObject jo = new JSONObject(strDataConnectLoaderImpls);
                for (java.util.Map.Entry<String, Object> entry : jo.toMap().entrySet()) {
                    if (entry.getValue() instanceof java.util.Map) {
                        java.util.Map<String, Object> cfg = (java.util.Map<String, Object>) entry.getValue();
                        String jarPath = (String) cfg.get("jar");
                        if (null != jarPath) {
                            jarPaths.add(jarPath);
                        }
                    }
                }
                return true;
            };
            loader.apply("DataConnectLoaderImpls.json");
            loader.apply("DataSetLoaderImpls.json");
            URL[] urls = new URL[jarPaths.size()];
            int i = 0;
            for (String jarPath : jarPaths) {
                log.info("load plug-in: {}", jarPath);
                urls[i] = new URL("file:" + jarPath);
                i++;
            }
            plugInClassLoader = new URLClassLoader(urls);
        } catch (Exception e) {
            throw new ConfigException("加载插件异常", e);
        }

        try {
            java.util.Map<String, DataConnectLoader> _dataConnectLoaders = new HashMap<>();
            String strDataConnectLoaderImpls = ResourcesReader.readStr(ConfigManager.class, "DataConnectLoaderImpls.json");
            JSONObject jo = new JSONObject(strDataConnectLoaderImpls);
            for (java.util.Map.Entry<String, Object> entry : jo.toMap().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String classPath;
                if (value instanceof java.util.Map) {
                    java.util.Map<String, Object> cfg = (java.util.Map<String, Object>) entry.getValue();
                    classPath = (String) cfg.get("class");
                } else {
                    classPath = (String) value;
                }
                Class<? extends DataConnectLoader> extClass = (Class<? extends DataConnectLoader>) plugInClassLoader.loadClass(classPath);

                DataConnectLoader impl = extClass.getConstructor().newInstance();
                _dataConnectLoaders.put(key, impl);
            }
            dataConnectLoaders = java.util.Map.copyOf(_dataConnectLoaders);
        } catch (Exception e) {
            throw new ConfigException("读取DataConnectLoader异常", e);
        }

        try {
            java.util.Map<String, DataSetLoader> _dataSetLoaders = new HashMap<>();
            String strDataSetLoaderImpls = ResourcesReader.readStr(ConfigManager.class, "DataSetLoaderImpls.json");
            JSONObject jo = new JSONObject(strDataSetLoaderImpls);
            for (java.util.Map.Entry<String, Object> entry : jo.toMap().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String classPath;
                if (value instanceof java.util.Map) {
                    java.util.Map<String, Object> cfg = (java.util.Map<String, Object>) entry.getValue();
                    classPath = (String) cfg.get("class");
                } else {
                    classPath = (String) value;
                }

                Class<? extends DataSetLoader> extClass = (Class<? extends DataSetLoader>) plugInClassLoader.loadClass(classPath);

                DataSetLoader impl = extClass.getConstructor().newInstance();
                _dataSetLoaders.put(key, impl);
            }
            dataSetLoaders = java.util.Map.copyOf(_dataSetLoaders);
        } catch (Exception e) {
            throw new ConfigException("读取DataSetLoader异常", e);
        }
    }

    private static void loadDataConnect() {
        if (null != dataConnects) {
            dataConnects.forEach((id, ds) -> {
                try {
                    ds.close();
                } catch (Exception e) {
                    log.warn("数据源{}关闭出错，可能产生资源泄露", id);
                }
            });
        }
        java.util.Map<String, DataConnect> _dataConnects = new HashMap<>();
        org.wowtools.dao.SqlUtil.queryWithJdbc(Constant.giscatConfigConnectionPool.getConnection(), (rs) -> {
            String id = rs.getString(1);
            try {
                String loaderName = rs.getString(2);
                DataConnectLoader loader = dataConnectLoaders.get(loaderName);
                if (null == loader) {
                    throw new ConfigException("DataConnectLoader 不存在:" + loaderName);
                }
                String config = rs.getString(3);
                java.util.Map configObj = Constant.jsonMapper.readValue(config, java.util.Map.class);
                DataConnect dataConnect = loader.load(id, configObj);
                dataConnect.test();
                _dataConnects.put(id, dataConnect);
                log.info("load DataConnect success {}", id);
            } catch (Exception e) {
                log.warn("加载DataConnect异常: {}", id, e);
            }
        }, "select id, loader, config from data_connect");
        dataConnects = java.util.Map.copyOf(_dataConnects);
    }

    private static void loadDataSet() {
        if (null != dataSets) {
            dataSets.forEach((id, ds) -> {
                try {
                    ds.close();
                } catch (Exception e) {
                    log.warn("数据集{}关闭出错，可能产生资源泄露", id);
                }
            });
        }
        java.util.Map<String, DataSet> _dataSets = new HashMap<>();
        org.wowtools.dao.SqlUtil.queryWithJdbc(Constant.giscatConfigConnectionPool.getConnection(), (rs) -> {
            String id = rs.getString(1);
            try {
                String loaderName = rs.getString(2);
                DataSetLoader loader = dataSetLoaders.get(loaderName);
                if (null == loader) {
                    throw new ConfigException("DataSetLoader 不存在:" + loaderName);
                }
                String config = rs.getString(3);
                java.util.Map configObj = Constant.jsonMapper.readValue(config, java.util.Map.class);
                String dataConnectId = rs.getString(4);
                DataConnect dataConnect = dataConnects.get(dataConnectId);
                if (null == dataConnect) {
                    throw new ConfigException("DataSet " + id + " 配置的数据源 " + dataConnectId + " 不存在");
                }
                DataSet dataSet = loader.load(id, configObj, dataConnect);
                dataSet.test();
                _dataSets.put(id, dataSet);
                log.info("load DataSet success {}", id);
            } catch (Exception e) {
                log.warn("加载DataSet异常: {}", id, e);
            }
        }, "select id, loader, config, data_connect_id from data_set");
        dataSets = java.util.Map.copyOf(_dataSets);
    }

    private static void loadLayerDataRule() {
        java.util.Map<String, List<LayerDataRule>> _layerDataRules = new HashMap<>();
        org.wowtools.dao.SqlUtil.queryWithJdbc(Constant.giscatConfigConnectionPool.getConnection(), (rs) -> {
            String layerId = rs.getString(1);
            byte minZoom = rs.getByte(2);
            byte maxZoom = rs.getByte(3);
            String dataSetId = rs.getString(4);
            String expression = rs.getString(5);
            byte orderNum = rs.getByte(6);
            try {
                ArrayList expressionObj = null == expression ? null : Constant.jsonMapper.readValue(expression, java.util.ArrayList.class);

                DataSet dataSet = dataSets.get(dataSetId);
                if (null == dataSet) {
                    throw new ConfigException("DataSet不存在 " + dataSetId);
                }

                LayerDataRule layerDataRule = new LayerDataRule(minZoom, maxZoom, dataSet, expressionObj, orderNum);
                List<LayerDataRule> ruleList = _layerDataRules.computeIfAbsent(layerId, k -> new LinkedList<>());
                ruleList.add(layerDataRule);
                log.info("load LayerDataRule success: layerId {} minZoom {} maxZoom {} dataSetId {}", layerId, minZoom, maxZoom, dataSetId);
            } catch (Exception e) {
                log.warn("加载LayerDataRule异常: layerId {} minZoom {} maxZoom {} dataSetId {}", layerId, minZoom, maxZoom, dataSetId, e);
            }
        }, "select layer_id,min_zoom,max_zoom,data_set_id,expression,order_num from layer_data_rule");
        java.util.Map<String, LayerDataRule[]> _layerDataRules1 = new HashMap<>(_layerDataRules.size());
        _layerDataRules.forEach((layerId, list) -> {
            list.sort(Comparator.comparingInt(LayerDataRule::getOrderNum));
            LayerDataRule[] arr = new LayerDataRule[list.size()];
            list.toArray(arr);
            _layerDataRules1.put(layerId, arr);
        });
        layerDataRules = java.util.Map.copyOf(_layerDataRules1);
    }

    private static void loadLayer() {
        java.util.Map<String, Layer> _layers = new HashMap<>();
        org.wowtools.dao.SqlUtil.queryWithJdbc(Constant.giscatConfigConnectionPool.getConnection(), (rs) -> {
            String id = rs.getString(1);
            try {
                LayerDataRule[] layerDataRule = layerDataRules.get(id);
                if (null == layerDataRule) {
                    throw new ConfigException("layerDataRule不存在 " + id);
                }

                Layer layer = new Layer(id, layerDataRule);
                _layers.put(id, layer);
                log.info("load layer success: {}", id);
            } catch (Exception e) {
                log.warn("加载 layer 异常: {}", id, e);
            }
        }, "select id from layer");
        layers = java.util.Map.copyOf(_layers);
    }

    private static void loadLayerInMaps() {
        class LayerCell {
            final Layer layer;
            final String name;
            final int zIndex;

            public LayerCell(Layer layer, String name, int zIndex) {
                this.layer = layer;
                this.name = name;
                this.zIndex = zIndex;
            }
        }
        java.util.Map<String, List<LayerCell>> _layerInMaps = new HashMap<>();
        org.wowtools.dao.SqlUtil.queryWithJdbc(Constant.giscatConfigConnectionPool.getConnection(), (rs) -> {
            String mapId = rs.getString(1);
            String layerId = rs.getString(2);
            String layerName = rs.getString(3);
            int zIndex = rs.getInt(4);
            try {
                Layer layer = layers.get(layerId);
                if (null == layer) {
                    throw new ConfigException("layer不存在 " + layerId);
                }
                LayerCell layerCell = new LayerCell(layer, layerName, zIndex);
                List<LayerCell> layerCellList = _layerInMaps.computeIfAbsent(mapId, k -> new LinkedList<>());
                layerCellList.add(layerCell);
                log.info("load layer_in_map success: mapId {} layerId {} layerName {} ", mapId, layerId, layerName);
            } catch (Exception e) {
                log.warn("加载 layer_in_map 异常: mapId {} layerId {} layerName {} ", mapId, layerId, layerName);
            }
        }, "select map_id,layer_id,layer_name,z_index from layer_in_map");
        java.util.Map<String, Map.MapLayer[]> _layerInMaps1 = new HashMap<>(_layerInMaps.size());
        _layerInMaps.forEach((mapId, layerCellList) -> {
            layerCellList.sort(Comparator.comparingInt(c -> c.zIndex));
            Map.MapLayer[] mapLayers = new Map.MapLayer[layerCellList.size()];
            int i = 0;
            for (LayerCell layerCell : layerCellList) {
                mapLayers[i] = new Map.MapLayer(layerCell.name, layerCell.layer);
                i++;
            }
            _layerInMaps1.put(mapId, mapLayers);
        });
        layerInMaps = java.util.Map.copyOf(_layerInMaps1);

    }

    private static void loadMap() {
        java.util.Map<String, Map> _maps = new HashMap<>();
        org.wowtools.dao.SqlUtil.queryWithJdbc(Constant.giscatConfigConnectionPool.getConnection(), (rs) -> {
            String id = rs.getString(1);
            try {
                Map.MapLayer[] layerInMap = layerInMaps.get(id);
                if (null == layerInMap) {
                    throw new ConfigException("layerInMap不存在 " + id);
                }
                Map map = new Map(id, layerInMap);
                _maps.put(id, map);
                log.info("load map success: {}", id);
            } catch (Exception e) {
                log.warn("加载 map 异常: {}", id, e);
            }
        }, "select id from map");
        maps = java.util.Map.copyOf(_maps);
    }

    private static void loadDataSetService() {
        java.util.Map<String, DataSetService> _dataSetServices = new HashMap<>(dataSets.size());
        dataSets.forEach((id, dataSet) -> _dataSetServices.put(id, new DataSetService(dataSet)));
        dataSetServices = java.util.Map.copyOf(_dataSetServices);
    }

    private static void loadLayerService() {
        java.util.Map<String, LayerService> _layerServices = new HashMap<>(layers.size());
        layers.forEach((id, layer) -> _layerServices.put(id, new LayerService(layer)));
        layerServices = java.util.Map.copyOf(_layerServices);
    }

    private static void loadMapSetService() {
        java.util.Map<String, MapService> _mapServices = new HashMap<>(maps.size());
        maps.forEach((id, map) -> _mapServices.put(id, new MapService(map)));
        mapServices = java.util.Map.copyOf(_mapServices);
    }

    private static void loadVectorTileService() {
        if (null != vectorTileServices) {
            vectorTileServices.forEach((id, service) -> {
                try {
                    service.close();
                } catch (IOException e) {
                    log.warn("VectorTileServices {}关闭出错，可能产生资源泄露", id);
                }
            });
        }
        java.util.Map<String, VectorTileService> _vectorTileServices = new HashMap<>();
        org.wowtools.dao.SqlUtil.queryWithJdbc(Constant.giscatConfigConnectionPool.getConnection(), (rs) -> {
            String mapId = rs.getString(1);
            try {
                long cacheTimeOut = rs.getLong(2);
                String strLayerConfigs = rs.getString(3);
                java.util.Map layerConfigs = Constant.jsonMapper.readValue(strLayerConfigs, java.util.Map.class);
                Map map = maps.get(mapId);
                if (null == map) {
                    throw new ConfigException("map不存在 " + mapId);
                }
                List<VectorTileService.VectorTileServiceLayer> vtsLayerList = new ArrayList<>(map.getMapLayers().length);
                for (Map.MapLayer mapLayer : map.getMapLayers()) {
                    java.util.Map layerConfig = (java.util.Map) layerConfigs.get(mapLayer.getName());
                    List<String> propertyNames = null != layerConfig && layerConfig.containsKey("propertyNames") ? (List<String>) layerConfig.get("propertyNames") : null;
                    int simplifyDistance = null != layerConfig && layerConfig.containsKey("simplifyDistance") ? (int) layerConfig.get("simplifyDistance") : 0;
                    VectorTileService.VectorTileServiceLayer vtsLayer = new VectorTileService.VectorTileServiceLayer(mapLayer, propertyNames, simplifyDistance);
                    vtsLayerList.add(vtsLayer);
                }
                VectorTileService.VectorTileServiceLayer[] vectorTileServiceLayers = new VectorTileService.VectorTileServiceLayer[vtsLayerList.size()];
                vtsLayerList.toArray(vectorTileServiceLayers);
                VectorTileService vectorTileService = new VectorTileService(map, cacheTimeOut, vectorTileServiceLayers);
                _vectorTileServices.put(mapId, vectorTileService);
                log.info("load VectorTileService success: {}", mapId);
            } catch (Exception e) {
                log.warn("加载 VectorTileService 异常: {}", mapId, e);
            }
        }, "select map_id,cache_time_out,layer_configs from vector_tile_service");
        vectorTileServices = java.util.Map.copyOf(_vectorTileServices);
    }

    public static @NotNull DataConnect getDataConnect(String id) {
        DataConnect o = dataConnects.get(id);
        if (null == o) {
            throw new InputException("DataConnect does not exist: " + id);
        }
        return o;
    }

    public static @NotNull DataSet getDataSet(String id) {
        DataSet o = dataSets.get(id);
        if (null == o) {
            throw new InputException("DataSet does not exist: " + id);
        }
        return o;
    }

    public static @NotNull Layer getLayer(String id) {
        Layer o = layers.get(id);
        if (null == o) {
            throw new InputException("Layer does not exist: " + id);
        }
        return o;
    }

    public static @NotNull Map getMap(String id) {
        Map o = maps.get(id);
        if (null == o) {
            throw new InputException("Map does not exist: " + id);
        }
        return o;
    }

    public static @NotNull DataSetService getDataSetService(String id) {
        DataSetService o = dataSetServices.get(id);
        if (null == o) {
            throw new InputException("DataSetService does not exist: " + id);
        }
        return o;
    }

    public static @NotNull LayerService getLayerService(String id) {
        LayerService o = layerServices.get(id);
        if (null == o) {
            throw new InputException("LayerService does not exist: " + id);
        }
        return o;
    }

    public static @NotNull MapService getMapService(String id) {
        MapService o = mapServices.get(id);
        if (null == o) {
            throw new InputException("MapService does not exist: " + id);
        }
        return o;
    }

    public static @NotNull VectorTileService getVectorTileService(String id) {
        VectorTileService o = vectorTileServices.get(id);
        if (null == o) {
            throw new InputException("VectorTileService does not exist: " + id);
        }
        return o;
    }

}
