package com.seedance.api.controller;

import com.seedance.api.config.VolcengineProperties;
import com.seedance.api.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理 REST API 控制器
 */
@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    private final VolcengineProperties properties;
    private final ConfigService configService;

    public ConfigController(VolcengineProperties properties, ConfigService configService) {
        this.properties = properties;
        this.configService = configService;
    }

    /**
     * 获取当前配置（脱敏）
     */
    @GetMapping
    public ResponseEntity<?> getConfig() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("baseUrl", properties.getBaseUrl());
            data.put("apiKey", properties.getMaskedApiKey());
            data.put("endpoint", properties.getEndpoint());
            data.put("isConfigured", properties.isValid());

            return ResponseEntity.ok(createSuccessResponse("获取配置成功", data));
        } catch (Exception e) {
            logger.error("获取配置失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("获取配置失败: " + e.getMessage()));
        }
    }

    /**
     * 更新配置
     */
    @PostMapping
    public ResponseEntity<?> updateConfig(@RequestBody ConfigUpdateRequest request) {
        try {
            if (request.getBaseUrl() == null || request.getBaseUrl().isEmpty()
                    || request.getApiKey() == null || request.getApiKey().isEmpty()
                    || request.getEndpoint() == null || request.getEndpoint().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("配置不完整，请填写所有必填项"));
            }

            // 更新配置属性
            properties.setBaseUrl(request.getBaseUrl());
            properties.setApiKey(request.getApiKey());
            properties.setEndpoint(request.getEndpoint());

            // 保存配置
            boolean success = configService.saveConfig(properties);

            if (success) {
                Map<String, Object> data = new HashMap<>();
                data.put("baseUrl", properties.getBaseUrl());
                data.put("apiKey", properties.getMaskedApiKey());
                data.put("endpoint", properties.getEndpoint());

                return ResponseEntity.ok(createSuccessResponse("配置保存成功", data));
            } else {
                return ResponseEntity.internalServerError().body(createErrorResponse("保存配置失败"));
            }
        } catch (Exception e) {
            logger.error("更新配置失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("更新配置失败: " + e.getMessage()));
        }
    }

    /**
     * 测试连接
     * 实际调用 Seedance API 验证配置
     */
    @PostMapping("/test")
    public ResponseEntity<?> testConnection() {
        try {
            if (!properties.isValid()) {
                return ResponseEntity.badRequest().body(createErrorResponse("配置不完整，请先配置 API 信息"));
            }

            // 实际调用 API 测试连接
            boolean success = configService.testConnection(properties);
            
            if (success) {
                Map<String, Object> data = new HashMap<>();
                data.put("baseUrl", properties.getBaseUrl());
                data.put("endpoint", properties.getEndpoint());
                data.put("testTime", java.time.LocalDateTime.now().toString());

                return ResponseEntity.ok(createSuccessResponse("连接测试成功！API 配置正确，可以正常调用 Seedance 服务", data));
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("连接测试失败：API 返回错误"));
            }
        } catch (Exception e) {
            logger.error("连接测试失败", e);
            return ResponseEntity.badRequest().body(createErrorResponse("连接测试失败: " + e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    /**
     * 配置更新请求 DTO
     */
    public static class ConfigUpdateRequest {
        private String baseUrl;
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
    }
}
