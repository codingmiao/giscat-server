package org.wowtools.giscatserver.main;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * @author liuyu
 * @date 2023/3/10
 */
public class ManageControllerTest extends ControllerTest {

    @Test
    public void reloadConfig() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/Manage/ReloadConfig/0")
        ).andReturn().getResponse().getContentAsString();
        res = mockMvc.perform(MockMvcRequestBuilders.get("/Manage/ReloadConfig/300")
        ).andReturn().getResponse().getContentAsString();
        Assert.assertEquals("success", res);
    }

    @Test
    public void clearVectorTile() throws Exception {
        String res = mockMvc.perform(MockMvcRequestBuilders.get("/Manage/ClearVectorTile/test_map")
        ).andReturn().getResponse().getContentAsString();
        Assert.assertEquals("success", res);
    }
}
