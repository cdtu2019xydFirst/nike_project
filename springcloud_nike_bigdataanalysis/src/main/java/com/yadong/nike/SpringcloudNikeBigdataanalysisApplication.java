package com.yadong.nike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.yadong.nike.bigdataanalysis.mapper")
public class SpringcloudNikeBigdataanalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringcloudNikeBigdataanalysisApplication.class, args);
    }

}
