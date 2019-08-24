package com.yadong.nike.passport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.yadong.nike.passport.feign")
public class SpringcloudNikePassportApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudNikePassportApplication.class, args);
    }

}
