package com.seedance.api.service;

import com.seedance.api.client.VideoGenerationClient;
import com.seedance.api.config.VolcengineProperties;
import com.seedance.api.model.VideoGenerationRequest;
import com.seedance.api.model.VideoGenerationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理服务
 */
@Service
public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    private static final String CONFIG_FILE = "application.yaml";
    private static final String EXTERNAL_CONFIG_DIR = "config";

    /**
     * 保存配置到文件
     */
    public boolean saveConfig(VolcengineProperties properties) {
        try {
            // 获取外部配置文件路径（项目根目录或用户目录）
            Path configPath = getExternalConfigPath();
            File configFile = configPath.toFile();

            // 确保目录存在
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }

            // 读取现有配置
            Map<String, Object> yamlConfig = new HashMap<>();
            if (configFile.exists()) {
                Yaml yaml = new Yaml();
                try (InputStream is = new FileInputStream(configFile)) {
                    Map<String, Object> existing = yaml.load(is);
                    if (existing != null) {
                        yamlConfig.putAll(existing);
                    }
                }
            }

            // 更新 volcengine 配置
            Map<String, String> volcengineConfig = new HashMap<>();
            volcengineConfig.put("base-url", properties.getBaseUrl());
            volcengineConfig.put("api-key", properties.getApiKey());
            volcengineConfig.put("endpoint", properties.getEndpoint());

            yamlConfig.put("volcengine", volcengineConfig);

            // 写入文件
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);
            try (Writer writer = new FileWriter(configFile)) {
                yaml.dump(yamlConfig, writer);
            }

            logger.info("配置已保存到: {}", configFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            logger.error("保存配置失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取外部配置文件路径
     * 优先级：1. 项目根目录 2. 用户主目录/.seedance/
     */
    public Path getExternalConfigPath() {
        // 尝试项目根目录
        Path projectPath = Paths.get(System.getProperty("user.dir"), CONFIG_FILE);
        if (Files.exists(projectPath)) {
            return projectPath;
        }

        // 如果项目根目录没有，尝试用户主目录
        String userHome = System.getProperty("user.home");
        Path userConfigDir = Paths.get(userHome, ".seedance");
        if (!Files.exists(userConfigDir)) {
            try {
                Files.createDirectories(userConfigDir);
            } catch (IOException e) {
                logger.warn("无法创建用户配置目录", e);
            }
        }

        return userConfigDir.resolve(CONFIG_FILE);
    }

    /**
     * 测试 API 连接
     * 实际调用 Seedance API 创建一个测试任务
     */
    public boolean testConnection(VolcengineProperties properties) {
        try {
            logger.info("开始测试 API 连接...");

            // 创建一个测试用的客户端
            com.seedance.api.config.ApiConfig config = new com.seedance.api.config.ApiConfig(
                properties.getBaseUrl(),
                properties.getApiKey(),
                properties.getEndpoint()
            );

            VideoGenerationClient client = new VideoGenerationClient(config);

            // 创建一个简单的测试请求（使用 getModelEndpoint() 获取模型端点）
            VideoGenerationRequest request = VideoGenerationRequest.builder()
                .model(properties.getModelEndpoint())
                .prompt("a simple test video, one second of blue sky, minimal movement")
                .duration(1)
                .build();

            // 尝试创建任务
            logger.info("发送测试请求到 Seedance API...");
            VideoGenerationResponse response = client.createVideoGenerationTask(request);

            if (response.isSuccess()) {
                logger.info("连接测试成功，任务ID: {}", response.getId());
                // 立即取消测试任务，避免浪费额度
                try {
                    client.cancelVideoGenerationTask(response.getId());
                    logger.info("已取消测试任务");
                } catch (Exception e) {
                    logger.warn("取消测试任务失败（不影响连接测试结果）: {}", e.getMessage());
                }
                return true;
            } else {
                logger.error("连接测试失败: {}", 
                    response.getError() != null ? response.getError().getMessage() : "未知错误");
                return false;
            }

        } catch (Exception e) {
            logger.error("连接测试失败: {}", e.getMessage(), e);
            throw new RuntimeException("连接测试失败: " + e.getMessage(), e);
        }
    }
}
