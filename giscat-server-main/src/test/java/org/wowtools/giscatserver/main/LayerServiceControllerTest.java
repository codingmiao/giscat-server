package org.wowtools.giscatserver.main;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wowtools.giscat.vector.pojo.Feature;
import org.wowtools.giscat.vector.pojo.FeatureCollection;
import org.wowtools.giscat.vector.pojo.converter.GeoJsonFeatureConverter;
import org.wowtools.giscat.vector.pojo.converter.ProtoFeatureConverter;
import org.wowtools.giscatserver.main.util.Constant;

/**
 * @author liuyu
 * @date 2023/3/9
 */
public class LayerServiceControllerTest extends ControllerTest {
    @Test
    public void query() throws Exception {
        byte[] res = mockMvc.perform(MockMvcRequestBuilders.get("/Layer/test_point_layer/Query")
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
        byte[] res = mockMvc.perform(MockMvcRequestBuilders.get("/Layer/test_point_layer/Query")
                .param("f", "pbf")
                .param("properties", "id")
        ).andReturn().getResponse().getContentAsByteArray();
        FeatureCollection fc = ProtoFeatureConverter.proto2featureCollection(res, Constant.geometryFactory);
        Assert.assertTrue(fc.getFeatures().size() > 0);
    }

    @Test
    public void nearest() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/Layer/test_point_layer/Nearest")
                .param("properties", "id")
                .param("x", "120")
                .param("y", "30")
                .param("n", "2")
        ).andReturn().getResponse().getContentAsString();
        FeatureCollection fc = GeoJsonFeatureConverter.fromGeoJsonFeatureCollection(res, Constant.geometryFactory);
        Assert.assertEquals(2, fc.getFeatures().size());
    }

    @Test
    public void nearest1() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/Layer/test_point_layer/Nearest")
                .param("properties", "id")
                .param("x", "120")
                .param("y", "30")
                .param("n", "2")
                .param("expression", "[\">=\", [\"get\", \"id\"], \"$1\"]")
                .param("bindParams", "{\"$1\":10}")
        ).andReturn().getResponse().getContentAsString();
        FeatureCollection fc = GeoJsonFeatureConverter.fromGeoJsonFeatureCollection(res, Constant.geometryFactory);
        Assert.assertEquals(2, fc.getFeatures().size());
        for (Feature feature : fc.getFeatures()) {
            int id = (int) feature.getProperties().get("id");
            Assert.assertTrue(id >= 10);
        }
    }
}
