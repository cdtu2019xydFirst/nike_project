package com.yadong.nike.passportConfig.config;

import com.yadong.nike.passportConfig.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : 熊亚东
 * @description :
 * @date : 2019/8/12 | 14:45
 **/
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        /*排除error错误请求*/
        registry.addInterceptor(authInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/error");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
