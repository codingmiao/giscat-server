/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */

package org.wowtools.giscatserver.main.util;

import java.util.List;
import java.util.concurrent.*;

/**
 * 异步执行任务的工具类
 *
 * @author liuyu
 * @date 2023/3/7
 */
public class AsyncTaskUtil {
    private static final ExecutorService pool = new ThreadPoolExecutor(0, Constant.threadNum,
            60000, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public static void executeAsyncTasks(List<Runnable> tasks) {
        int n = tasks.size();
        Semaphore semaphore = new Semaphore(0);
        for (Runnable task : tasks) {
            pool.execute(() -> {
                try {
                    task.run();
                } finally {
                    semaphore.release();
                }
            });
        }
        try {
            semaphore.acquire(n);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
