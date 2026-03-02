package com.seedance.api.service;

import com.seedance.api.model.VolcengineConfig;
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
public class ConfigService {
    
    private static final String CONFIG_FILE = "src/main/resources/application.yaml";
    private static VolcengineConfig currentConfig;
    
    static {
        // 初始化时加载配置
        currentConfig = loadConfigFromFile();
    }
    
    /**
     * 获取当前配置
     */
    public static VolcengineConfig getConfig() {
        if (currentConfig == null) {
            currentConfig = new VolcengineConfig();
            currentConfig.setBaseUrl("https://ark.cn-beijing.volces.com");
        }
        return currentConfig;
    }
    
    /**
     * 从文件加载配置
     */
    public static VolcengineConfig loadConfigFromFile() {
        try {
            // 尝试从工作目录加载
            String configPath = System.getProperty("user.dir") + "/" + CONFIG_FILE;
            if (!Files.exists(Paths.get(configPath))) {
                // 如果在 backend 目录下运行
                configPath = System.getProperty("user.dir") + "/backend/" + CONFIG_FILE;
            }
            
            File configFile = new File(configPath);
            if (!configFile.exists()) {
                System.out.println("配置文件不存在，使用默认配置");
                return createDefaultConfig();
            }
            
            Yaml yaml = new Yaml();
            try (InputStream inputStream = new FileInputStream(configFile)) {
                Map<String, Object> config = yaml.load(inputStream);
                return parseConfig(config);
            }
        } catch (Exception e) {
            System.err.println("加载配置失败: " + e.getMessage());
            return createDefaultConfig();
        }
    }
    
    /**
     * 保存配置到文件
     */
    public static boolean saveConfig(VolcengineConfig config) {
        try {
            String configPath = System.getProperty("user.dir") + "/" + CONFIG_FILE;
            File configFile = new File(configPath);
            
            // 如果不在根目录，尝试 backend 目录
            if (!configFile.getParentFile().exists()) {
                configPath = System.getProperty("user.dir") + "/backend/" + CONFIG_FILE;
                configFile = new File(configPath);
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
            volcengineConfig.put("base-url", config.getBaseUrl());
            volcengineConfig.put("api-key", config.getApiKey());
            volcengineConfig.put("endpoint", config.getEndpoint());
            
            yamlConfig.put("volcengine", volcengineConfig);
            
            // 写入文件
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            
            Yaml yaml = new Yaml(options);
            try (Writer writer = new FileWriter(configFile)) {
                yaml.dump(yamlConfig, writer);
            }
            
            // 更新内存中的配置
            currentConfig = config;
            
            System.out.println("配置已保存到: " + configFile.getAbsolutePath());
            return true;
            
        } catch (Exception e) {
            System.err.println("保存配置失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新配置（不保存到文件）
     */
    public static void updateConfig(VolcengineConfig config) {
        currentConfig = config;
    }
    
    /**
     * 创建默认配置
     */
    private static VolcengineConfig createDefaultConfig() {
        VolcengineConfig config = new VolcengineConfig();
        config.setBaseUrl("https://ark.cn-beijing.volces.com");
        config.setApiKey("");
        config.setEndpoint("");
        return config;
    }
    
    /**
     * 解析配置
     */
    @SuppressWarnings("unchecked")
    private static VolcengineConfig parseConfig(Map<String, Object> config) {
        VolcengineConfig volcengineConfig = new VolcengineConfig();
        
        if (config != null && config.containsKey("volcengine")) {
            Map<String, Object> volcengine = (Map<String, Object>) config.get("volcengine");
            volcengineConfig.setBaseUrl((String) volcengine.get("base-url"));
            volcengineConfig.setApiKey((String) volcengine.get("api-key"));
            volcengineConfig.setEndpoint((String) volcengine.get("endpoint"));
        }
        
        return volcengineConfig;
    }
}
