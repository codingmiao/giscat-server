/*
 * Copyright (c) 2022- "giscat (https://github.com/codingmiao/giscat)"
 *
 * 本项目采用自定义版权协议，在不同行业使用时有不同约束，详情参阅：
 *
 * https://github.com/codingmiao/giscat/blob/main/LICENSE
 */
package org.wowtools.giscatserver.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author liuyu
 * @date 2023/2/24
 */
@SpringBootApplication
public class StartUp {
    public static void main(String[] args) {
        SpringApplication.run(StartUp.class, args);

    }

    @PostConstruct
    private void init() {
        ConfigManager.load();
    }
}
