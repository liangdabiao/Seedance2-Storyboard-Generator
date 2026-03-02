package com.seedance.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 外部配置加载器
 * 启动时从外部配置文件加载配置
 */
@Component
public class ExternalConfigLoader implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExternalConfigLoader.class);

    private final VolcengineProperties properties;

    public ExternalConfigLoader(VolcengineProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 尝试从外部配置文件加载
        loadFromExternalConfig();
    }

    private void loadFromExternalConfig() {
        try {
            // 检查项目根目录
            Path projectPath = Paths.get(System.getProperty("user.dir"), "application.yaml");
            if (Files.exists(projectPath)) {
                loadConfigFile(projectPath);
                return;
            }

            // 检查用户主目录
            String userHome = System.getProperty("user.home");
            Path userPath = Paths.get(userHome, ".seedance", "application.yaml");
            if (Files.exists(userPath)) {
                loadConfigFile(userPath);
                return;
            }

            logger.info("未找到外部配置文件，使用默认配置");

        } catch (Exception e) {
            logger.warn("加载外部配置失败: {}", e.getMessage());
        }
    }

    private void loadConfigFile(Path configPath) {
        try (InputStream is = new FileInputStream(configPath.toFile())) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(is);

            if (config != null && config.containsKey("volcengine")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> volcengine = (Map<String, Object>) config.get("volcengine");

                if (volcengine.containsKey("api-key")) {
                    properties.setApiKey((String) volcengine.get("api-key"));
                }
                if (volcengine.containsKey("base-url")) {
                    properties.setBaseUrl((String) volcengine.get("base-url"));
                }
                if (volcengine.containsKey("endpoint")) {
                    properties.setEndpoint((String) volcengine.get("endpoint"));
                }

                logger.info("已从外部配置文件加载: {}", configPath);
            }
        } catch (Exception e) {
            logger.error("读取配置文件失败: {}", e.getMessage());
        }
    }
}
