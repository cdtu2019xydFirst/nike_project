package com.yadong.nike;

import com.yadong.nike.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yadong.nike.shoppingcart.feign")
@MapperScan(basePackages = "com.yadong.nike.shoppingcart.mapper")
public class SpringcloudNikeShoppingcartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudNikeShoppingcartApplication.class, args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1, 1);
    }

}
