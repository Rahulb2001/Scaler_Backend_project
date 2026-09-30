package com.scaler.backend.productcatalog.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * This is the ONLY RestTemplate bean in the app, and it's deliberately
     * load-balanced: it's used exclusively for calling other services by
     * their Eureka application name (e.g. "http://userservice/..."), never
     * for hitting a real external host directly. FakeStoreProductService
     * needs a plain, non-load-balanced client for fakestoreapi.com, so it
     * builds its own RestTemplate from a RestTemplateBuilder instead of
     * depending on this bean.
     */
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
