package com.seedance.api.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * API 配置类
 * 管理火山引擎 API 的 Base URL、API Key 等配置
 */
public class ApiConfig {
    
    // 火山方舟平台默认地址
    private static final String DEFAULT_BASE_URL = "https://ark.cn-beijing.volces.com";
    
    private String baseUrl;
    private String apiKey;
    private String modelEndpoint; // 推理接入点 ID
    
    public ApiConfig() {
        this.baseUrl = DEFAULT_BASE_URL;
    }
    
    public ApiConfig(String baseUrl, String apiKey, String modelEndpoint) {
        this.baseUrl = baseUrl != null ? baseUrl : DEFAULT_BASE_URL;
        this.apiKey = apiKey;
        this.modelEndpoint = modelEndpoint;
    }
    
    /**
     * 从配置文件加载配置
     */
    public static ApiConfig fromConfigFile(String configPath) {
        try (InputStream inputStream = ApiConfig.class.getClassLoader()
                .getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new RuntimeException("配置文件未找到: " + configPath);
            }
            
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);
            
            Map<String, Object> volcengine = (Map<String, Object>) config.get("volcengine");
            if (volcengine == null) {
                throw new RuntimeException("配置文件中缺少 'volcengine' 配置节点");
            }
            
            String baseUrl = (String) volcengine.get("base-url");
            String apiKey = (String) volcengine.get("api-key");
            String endpoint = (String) volcengine.get("endpoint");
            
            return new ApiConfig(baseUrl, apiKey, endpoint);
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件失败: " + e.getMessage(), e);
        }
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
    
    public String getModelEndpoint() {
        return modelEndpoint;
    }
    
    public void setModelEndpoint(String modelEndpoint) {
        this.modelEndpoint = modelEndpoint;
    }
    
    /**
     * 获取视频生成 API 路径
     */
    public String getVideoGenerationPath() {
        return "/api/v3/contents/generations";
    }
    
    /**
     * 获取查询任务 API 路径
     */
    public String getVideoTaskQueryPath(String taskId) {
        return "/api/v3/contents/generations/" + taskId;
    }
    
    /**
     * 获取任务列表 API 路径
     */
    public String getVideoTaskListPath() {
        return "/api/v3/contents/generations";
    }
    
    /**
     * 获取取消任务 API 路径
     */
    public String getVideoTaskCancelPath(String taskId) {
        return "/api/v3/contents/generations/" + taskId + "/cancel";
    }
    
    @Override
    public String toString() {
        return "ApiConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiKey='" + (apiKey != null ? "***" : "null") + '\'' +
                ", modelEndpoint='" + modelEndpoint + '\'' +
                '}';
    }
}
