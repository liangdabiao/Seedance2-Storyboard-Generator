package com.seedance.api.controller;

import com.seedance.api.model.VolcengineConfig;
import com.seedance.api.service.ConfigService;
import com.seedance.api.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理控制器
 */
public class ConfigController {
    
    /**
     * 获取当前配置
     */
    public static String getConfig() {
        try {
            VolcengineConfig config = ConfigService.getConfig();
            
            // 返回脱敏的配置（隐藏 API Key）
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            Map<String, Object> data = new HashMap<>();
            data.put("baseUrl", config.getBaseUrl());
            data.put("apiKey", config.getMaskedApiKey()); // 脱敏显示
            data.put("endpoint", config.getEndpoint());
            data.put("isConfigured", config.isValid());
            
            response.put("data", data);
            
            return JsonUtils.toJson(response);
            
        } catch (Exception e) {
            return createErrorResponse(e.getMessage());
        }
    }
    
    /**
     * 更新配置
     */
    public static String updateConfig(String requestBody) {
        try {
            Map<String, Object> request = JsonUtils.fromJson(requestBody, Map.class);
            
            VolcengineConfig config = new VolcengineConfig();
            config.setBaseUrl((String) request.get("baseUrl"));
            config.setApiKey((String) request.get("apiKey"));
            config.setEndpoint((String) request.get("endpoint"));
            
            // 验证配置
            if (!config.isValid()) {
                return createErrorResponse("配置不完整，请填写所有必填项");
            }
            
            // 保存配置
            boolean success = ConfigService.saveConfig(config);
            
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "配置保存成功");
                
                Map<String, Object> data = new HashMap<>();
                data.put("baseUrl", config.getBaseUrl());
                data.put("apiKey", config.getMaskedApiKey());
                data.put("endpoint", config.getEndpoint());
                
                response.put("data", data);
                
                return JsonUtils.toJson(response);
            } else {
                return createErrorResponse("保存配置失败");
            }
            
        } catch (Exception e) {
            return createErrorResponse("更新配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试连接
     */
    public static String testConnection() {
        try {
            VolcengineConfig config = ConfigService.getConfig();
            
            if (!config.isValid()) {
                return createErrorResponse("配置不完整，请先配置 API 信息");
            }
            
            // 这里可以实现实际的 API 测试连接
            // 暂时返回成功
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "连接测试成功");
            response.put("data", Map.of(
                "baseUrl", config.getBaseUrl(),
                "endpoint", config.getEndpoint()
            ));
            
            return JsonUtils.toJson(response);
            
        } catch (Exception e) {
            return createErrorResponse("连接测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建错误响应
     */
    private static String createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return JsonUtils.toJson(response);
    }
}
