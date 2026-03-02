package com.seedance.api.config;

import com.seedance.api.client.HttpClient;
import com.seedance.api.client.VideoGenerationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API 客户端配置
 */
@Configuration
public class ClientConfig {

    /**
     * 配置 HttpClient
     */
    @Bean
    public HttpClient httpClient(VolcengineProperties properties) {
        ApiConfig config = new ApiConfig(
            properties.getBaseUrl(),
            properties.getApiKey(),
            properties.getEndpoint()
        );
        return new HttpClient(config);
    }

    /**
     * 配置 VideoGenerationClient
     */
    @Bean
    public VideoGenerationClient videoGenerationClient(HttpClient httpClient, VolcengineProperties properties) {
        ApiConfig config = new ApiConfig(
            properties.getBaseUrl(),
            properties.getApiKey(),
            properties.getEndpoint()
        );
        return new VideoGenerationClient(config, httpClient);
    }
}
