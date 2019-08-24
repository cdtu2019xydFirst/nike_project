package com.yadong.nike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.yadong.nike.item.feign")
public class SpringcloudNikeItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudNikeItemApplication.class, args);
    }

}
