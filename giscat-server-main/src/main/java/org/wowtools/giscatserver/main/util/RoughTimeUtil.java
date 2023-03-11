/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 粗粒度的时间戳获取工具，由于系统频繁地System.currentTimeMillis()，做一个定时器统一获取时间减少性能开销
 *
 * @author liuyu
 * @date 2023/3/7
 */
public class RoughTimeUtil {
    private static long timestamp = System.currentTimeMillis();

    static {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timestamp = System.currentTimeMillis();
            }
        };
        timer.schedule(task, 0, Constant.roughTimePrecision);
    }

    public static long getTimestamp() {
        return timestamp;
    }
}
