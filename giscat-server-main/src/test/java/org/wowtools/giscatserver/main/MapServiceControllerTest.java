package org.wowtools.giscatserver.main;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wowtools.giscat.vector.mvt.MvtParser;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscat.vector.pojo.FeatureCollection;
import org.wowtools.giscat.vector.pojo.converter.GeoJsonFeatureConverter;
import org.wowtools.giscat.vector.pojo.converter.ProtoFeatureConverter;
import org.wowtools.giscatserver.main.util.Constant;

import java.util.ArrayList;

/**
 * @author liuyu
 * @date 2023/3/9
 */
public class MapServiceControllerTest extends ControllerTest {
    @Test
    public void query() throws Exception {
        byte[] res = mockMvc.perform(MockMvcRequestBuilders.get("/Map/test_map/Query")
                .param("f", "pbf")
                .param("properties", "id")
                .param("expression", "[\">=\", [\"get\", \"id\"], \"$1\"]")
                .param("bindParams", "{\"$1\":10}")
        ).andReturn().getResponse().getContentAsByteArray();
        FeatureCollection fc = ProtoFeatureConverter.proto2featureCollection(res, Constant.geometryFactory);
        Assert.assertTrue(fc.getFeatures().size() > 0);
        for (Feature feature : fc.getFeatures()) {
            int id = (int) feature.getProperties().get("id");
            Assert.assertTrue(id >= 10);
        }
    }

    @Test
    public void query1() throws Exception {
        byte[] res = mockMvc.perform(MockMvcRequestBuilders.get("/Map/test_map/Query")
                .param("f", "pbf")
                .param("properties", "id")
        ).andReturn().getResponse().getContentAsByteArray();
        FeatureCollection fc = ProtoFeatureConverter.proto2featureCollection(res, Constant.geometryFactory);
        Assert.assertTrue(fc.getFeatures().size() > 0);
    }

    @Test
    public void nearest() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/Map/test_map/Nearest")
                .param("properties", "id")
                .param("x", "120")
                .param("y", "30")
                .param("n", "2")
        ).andReturn().getResponse().getContentAsString();
        FeatureCollection fc = GeoJsonFeatureConverter.fromGeoJsonFeatureCollection(res, Constant.geometryFactory);
        Assert.assertArrayEquals(new Object[]{1, 3, 5}, ((ArrayList) fc.getHeaders().get("featureIndexes")).toArray());
    }

    @Test
    public void nearest1() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/Map/test_map/Nearest")
                .param("properties", "id")
                .param("x", "120")
                .param("y", "30")
                .param("n", "2")
                .param("expression", "[\">=\", [\"get\", \"id\"], \"$1\"]")
                .param("bindParams", "{\"$1\":10}")
        ).andReturn().getResponse().getContentAsString();
        FeatureCollection fc = GeoJsonFeatureConverter.fromGeoJsonFeatureCollection(res, Constant.geometryFactory);
        Assert.assertArrayEquals(new Object[]{1, 3, 5}, ((ArrayList) fc.getHeaders().get("featureIndexes")).toArray());
        for (Feature feature : fc.getFeatures()) {
            int id = (int) feature.getProperties().get("id");
            Assert.assertTrue(id >= 10);
        }
    }

    @Test
    public void exportVectorTile() throws Exception {
        byte[] res = mockMvc.perform(MockMvcRequestBuilders.get("/Map/test_map/VectorTile/3/6/3")
        ).andReturn().getResponse().getContentAsByteArray();
        MvtParser.MvtFeatureLayer[] fsl = MvtParser.parse2TileCoords(res, Constant.geometryFactory);
//        MvtParser.MvtFeatureLayer[] fs = MvtParser.parse2Wgs84Coords((byte)3,6,3,res, Constant.geometryFactory);
        Assert.assertTrue(fsl.length > 0);
    }

    @Test
    public void exportVectorTile1() throws Exception {
        byte[] res = mockMvc.perform(MockMvcRequestBuilders.get("/Map/test_map/VectorTile/3/6/3")
                .param("expression", "[\">=\", [\"get\", \"id\"], \"$1\"]")
                .param("bindParams", "{\"$1\":10}")
        ).andReturn().getResponse().getContentAsByteArray();
        MvtParser.MvtFeatureLayer[] fsl = MvtParser.parse2TileCoords(res, Constant.geometryFactory);
//        MvtParser.MvtFeatureLayer[] fs = MvtParser.parse2Wgs84Coords((byte)3,6,3,res, Constant.geometryFactory);
        Assert.assertTrue(fsl.length > 0);
        for (MvtParser.MvtFeatureLayer fs : fsl) {
            for (Feature feature : fs.getFeatures()) {
                Long id = (Long) feature.getProperties().get("id");
                Assert.assertTrue(id >= 10);
            }
        }
    }
}
