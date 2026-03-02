package com.seedance.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 火山引擎配置属性
 */
@Component
@ConfigurationProperties(prefix = "volcengine")
public class VolcengineProperties {

    private String baseUrl = "https://ark.cn-beijing.volces.com";
    private String apiKey;
    private String endpoint;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * 检查配置是否有效
     */
    public boolean isValid() {
        return apiKey != null && !apiKey.isEmpty()
                && endpoint != null && !endpoint.isEmpty();
    }

    /**
     * 脱敏显示 API Key
     */
    public String getMaskedApiKey() {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
