/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author liuyu
 * @date 2023/3/7
 */
public class ServletUtil {
    private static final String bytesContentType = "application/octet-stream";

    /**
     * 将bytes写进HttpServletResponse
     * @param bytes bytes
     * @param response HttpServletResponse
     */
    public static void exportByte(byte[] bytes, HttpServletResponse response) {
        response.setContentType(bytesContentType);
        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes);
            os.flush();
        } catch (org.apache.catalina.connector.ClientAbortException e) {
            //地图移动时客户端主动取消， 产生异常"你的主机中的软件中止了一个已建立的连接"，无需处理
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
