package com.seedance.api.service;

import com.seedance.api.config.VolcengineProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
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

    /**
     * 保存配置到文件
     */
    public boolean saveConfig(VolcengineProperties properties) {
        try {
            // 查找配置文件路径
            String configPath = findConfigFilePath();
            File configFile = new File(configPath);

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
     * 查找配置文件路径
     */
    private String findConfigFilePath() {
        // 可能的配置文件位置
        String[] possiblePaths = {
            System.getProperty("user.dir") + "/backend/src/main/resources/" + CONFIG_FILE,
            System.getProperty("user.dir") + "/src/main/resources/" + CONFIG_FILE,
            "src/main/resources/" + CONFIG_FILE
        };

        for (String path : possiblePaths) {
            if (Files.exists(Paths.get(path))) {
                return path;
            }
        }

        // 默认使用第一个路径
        return possiblePaths[0];
    }
}
