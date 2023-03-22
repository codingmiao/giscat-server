# giscat-server

giscat-webgis服务,简单、高效、开放地支撑二维gis应用开发

## 简单

基于常用Java框架、以mapbox-gl所用接口为蓝本、以OGC为规范，学习成本极低。

## 高效

性能是geoserver的数倍：更高压缩率的数据结构、更合理的缓存利用、流传输等技术。

## 开放

层次化的模块结构，可以支持插件扩展，亦可单独提取某个模块作深度开发。

[点击这里查看详细文档](https://doc.giscat.top/giscat-server/quickstart/)

# 接口演示

giscat-server提供了把您的数据快速发布为GIS服务的能力，下面的示例中，我们先体验一下， 从[阿里datav](https://datav.aliyun.com/portal/school/atlas/area_selector)下载的全国各省份数据发布成的接口：


## 查询名称为"云南省"的省：
```shell
curl 'https://map.wowtools.org/giscat-server/DataSet/giscat_testdata_test_polygon/Query?properties=id,name&expression=%5B%22%3D%3D%22%2C%5B%22get%22%2C%22name%22%5D%2C%22%E4%BA%91%E5%8D%97%E7%9C%81%22%5D'
```
其中，expression参数是urlencode后的`["==",["get","name"],"云南省"]`

## 查询距离点(101.5,20.4)最近的3个省：

```shell
curl 'https://map.wowtools.org/giscat-server/DataSet/giscat_testdata_test_polygon/Nearest?properties=id,name&x=101.5&y=20.4&n=3'
```

## 输入一条线，查询这条线穿过哪些省：

```shell
curl 'https://map.wowtools.org/giscat-server/DataSet/giscat_testdata_test_polygon/Query?properties=id,name&expression=%5B%22geoIntersects%22%2C%22LINESTRING(100.2%2020.3%2C120.1%2030.4)%22%5D'
```
其中，expression参数是urlencode后的`["geoIntersects","LINESTRING(100.2 20.3,120.1 30.4)"]`


上述查询返回值均为geojson，形如:

```json5
{
	"type": "FeatureCollection",
	"features": [{
		"type": "Feature",
		"geometry": {
			"type": "MultiPolygon",
			"coordinates": [
              //....
            ]
		},
		"properties": {
			"name": "浙江省",
			"id": 11
		}
	}, {
		"type": "Feature",
		"geometry": {
			"type": "MultiPolygon",
			"coordinates": [
              //....
            ]
		},
		"properties": {
			"name": "安徽省",
			"id": 12
		}
	}
      //...
}
```

## 构造一个矢量瓦片图层并在地图上显示：

```js

    mapboxgl.accessToken = 'pk.eyJ1IjoiaW1saXV5dSIsImEiOiJjbDM4aHM4eXowMDBpM2RvZGdxdGZjeWMxIn0.mYtay02E_Z4iYOsDx3IdoA';
    // 新建一个空白地图
    const map = new mapboxgl.Map({
        container: 'map',
        style: {
            "version": 8,
            "sources": {},
            "layers": []
        },
        refreshExpiredTiles: false,//瓦片过期后自动刷新，这里为了测试方便调整为false
        center: [102.712251, 25.040609],
        zoom: 4
    });
    map.on('load', function () {
        //添加刚才发布的mvt数据源
        map.addSource('tile', {
            "type": "vector",
            "tiles": [
                'https://map.wowtools.org/giscat-server/Map/test_map/VectorTile/{z}/{x}/{y}'
            ],
            "minZoom": 1,
            "maxZoom": 22
        })
        //添加各图层
        map.addLayer({
            "id": "polygons",
            "type": "fill",
            "source": "tile",// 上一步添加的数据源id
            "source-layer": "polygons",// source-layer和mvt服务中的图层名对应
            "layout": {"visibility": "visible"},
            "paint": {"fill-color": '#51bbd6', "fill-opacity": 0.3, "fill-outline-color": '#0000ff'}
        })

        // map.addLayer  ...

        //矢量瓦片图层可以被点击
        map.on('click', 'polygons', (e) => {
            console.log(e.features[0])
        })
    })

```

## 小结

至此，我们已对giscat-server的能力有了大致的了解，它可以：

1、传入一个json格式的表达式查询数据，无论是属性查询还是空间过滤都可以，熟悉mapbox的小伙伴会发现，这个json表达式其实是[mapbox expressions](https://docs.mapbox.com/mapbox-gl-js/style-spec/expressions/),所以学习成本很低；

2、最邻近查询，同样可以用mapbox expressions表达式过滤；

3、生成矢量瓦片，矢量瓦片也是可以用mapbox expressions表达式过滤的。

另外，功能1、2中的返回结果可以指定参数`f=pbf`，使其返回ProtoFeature二进制格式，比默认的geojson格式具有更高的压缩率和更好的性能，对数据量较大的查询很有用处。
同时giscat配备了ProtoFeature字节码还原为json对象的[java工具](https://github.com/codingmiao/giscat/tree/main/giscat-vector/giscat-vector-pojo#%E4%B8%8Eprotobuf%E4%BA%92%E8%BD%AC)以及[js工具](https://www.npmjs.com/package/giscatjs)，方便大家快捷地在java和js中使用。

[点击这里查看详细文档](https://doc.giscat.top/giscat-server/quickstart/)
