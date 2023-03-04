/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main;

import cn.com.enersun.mywebgis.mywebgisservice.common.exception.ConfigException;
import cn.com.enersun.mywebgis.mywebgisservice.common.exception.InputException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.wowtools.common.utils.ResourcesReader;
import org.wowtools.giscatserver.dataconnect.api.DataConnect;
import org.wowtools.giscatserver.dataconnect.api.DataConnectLoader;
import org.wowtools.giscatserver.dataset.api.DataSet;
import org.wowtools.giscatserver.main.service.DataSetService;
import org.wowtools.giscatserver.main.service.LayerService;
import org.wowtools.giscatserver.main.service.MapService;
import org.wowtools.giscatserver.main.structure.Layer;
import org.wowtools.giscatserver.main.structure.LayerDataRule;
import org.wowtools.giscatserver.main.structure.Map;
import org.wowtools.giscatserver.main.util.Constant;

import java.util.HashMap;


/**
 * 配置信息的读取与管理
 *
 * @author liuyu
 * @date 2023/3/4
 */
@Slf4j
public class ConfigManager {
    private static final java.util.Map<String, DataConnectLoader> dataConnectLoaders;

    static {
        try {
            java.util.Map<String, DataConnectLoader> _dataConnectLoaders = new HashMap<>();
            String strDataConnectLoaderImpls = ResourcesReader.readStr(ConfigManager.class, "DataConnectLoaderImpls.json");
            JSONObject jo = new JSONObject(strDataConnectLoaderImpls);
            for (java.util.Map.Entry<String, Object> entry : jo.toMap().entrySet()) {
                String classPath = (String) entry.getValue();
                String key = entry.getKey();
                DataConnectLoader impl = (DataConnectLoader) Class.forName(classPath).getConstructor().newInstance();
                _dataConnectLoaders.put(key, impl);
            }
            dataConnectLoaders = java.util.Map.copyOf(_dataConnectLoaders);
        } catch (Exception e) {
            throw new ConfigException("读取DataConnectLoader异常", e);
        }
    }

    private static java.util.Map<String, DataConnect> dataConnects;

    private static java.util.Map<String, DataSet> dataSets;

    private static java.util.Map<String, LayerDataRule> layerDataRules;

    private static java.util.Map<String, Layer> layers;

    private static java.util.Map<String, Map> maps;

    private static java.util.Map<String, DataSetService> dataSetServices;

    private static java.util.Map<String, LayerService> layerServices;

    private static java.util.Map<String, MapService> mapServices;

    public static synchronized void load() {
        log.info("ConfigManager load start");
        loadDataConnect();
        log.info("ConfigManager load success");
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
                DataConnect dataConnect = loader.load(configObj);
                dataConnect.test();
                _dataConnects.put(id, dataConnect);
            } catch (Exception e) {
                log.warn("加载DataConnect异常: {}", id, e);
            }
        }, "select id, loader, config from data_connect");
        dataConnects = java.util.Map.copyOf(_dataConnects);
    }


    public static DataConnect getDataConnect(String id) {
        DataConnect o = dataConnects.get(id);
        if (null == o) {
            throw new InputException("DataConnect does not exist: " + id);
        }
        return o;
    }

    public static DataSet getDataSet(String id) {
        DataSet o = dataSets.get(id);
        if (null == o) {
            throw new InputException("DataSet does not exist: " + id);
        }
        return o;
    }

    public static LayerDataRule getLayerDataRule(String id) {
        LayerDataRule o = layerDataRules.get(id);
        if (null == o) {
            throw new InputException("LayerDataRule does not exist: " + id);
        }
        return o;
    }

    public static Layer getLayer(String id) {
        Layer o = layers.get(id);
        if (null == o) {
            throw new InputException("Layer does not exist: " + id);
        }
        return o;
    }

    public static Map getMap(String id) {
        Map o = maps.get(id);
        if (null == o) {
            throw new InputException("Map does not exist: " + id);
        }
        return o;
    }

    public static DataSetService getDataSetService(String id) {
        DataSetService o = dataSetServices.get(id);
        if (null == o) {
            throw new InputException("DataSetService does not exist: " + id);
        }
        return o;
    }

    public static LayerService getLayerService(String id) {
        LayerService o = layerServices.get(id);
        if (null == o) {
            throw new InputException("LayerService does not exist: " + id);
        }
        return o;
    }

    public static MapService getMapService(String id) {
        MapService o = mapServices.get(id);
        if (null == o) {
            throw new InputException("MapService does not exist: " + id);
        }
        return o;
    }
}
