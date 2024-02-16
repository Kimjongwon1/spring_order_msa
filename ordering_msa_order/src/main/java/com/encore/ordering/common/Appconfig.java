package com.encore.ordering.common;

import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Appconfig {
    @Bean
//    eureka 등록된 서비스명을 사용해서 내부서비스 호출 할 수 있게 해주는 어노테이션
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
