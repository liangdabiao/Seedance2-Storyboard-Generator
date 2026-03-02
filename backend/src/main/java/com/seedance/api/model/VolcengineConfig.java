package com.seedance.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 火山引擎配置 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VolcengineConfig {
    
    private String baseUrl;
    private String apiKey;
    private String endpoint;
    
    public VolcengineConfig() {
    }
    
    public VolcengineConfig(String baseUrl, String apiKey, String endpoint) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.endpoint = endpoint;
    }
    
    // Getters and Setters
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
     * 检查配置是否完整
     */
    public boolean isValid() {
        return baseUrl != null && !baseUrl.isEmpty()
                && apiKey != null && !apiKey.isEmpty()
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
    
    @Override
    public String toString() {
        return "VolcengineConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiKey='" + getMaskedApiKey() + '\'' +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
