package com.panda.aoodds.esport;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@EnableDubbo
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.panda.aoodds"},exclude = MongoAutoConfiguration.class)
public class AooddsEsportServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AooddsEsportServiceApplication.class, args);
    }
}