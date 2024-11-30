package com.company.encryptedtrafficclassifier;

import cn.dev33.satoken.SaManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class EncryptedTrafficClassifierApplication {

    public static void main(String[] args) {
//        String prop = System.getProperty("jna.library.path");
//        if (prop == null || prop.isEmpty()) {
//            prop = "C:/Windows/System32/Npcap";
//        } else {
//            prop += ";C:/Windows/System32/Npcap";
//        }
//        System.setProperty("jna.library.path", prop);
        SpringApplication.run(EncryptedTrafficClassifierApplication.class, args);

        log.info("加密流量分类系统启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

}