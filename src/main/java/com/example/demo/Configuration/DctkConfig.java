package com.example.demo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class DctkConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
