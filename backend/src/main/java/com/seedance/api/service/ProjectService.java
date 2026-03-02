package com.seedance.api.service;

import com.seedance.api.model.Asset;
import com.seedance.api.model.Episode;
import com.seedance.api.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 项目服务
 * 扫描本地 projects 文件夹获取项目数据
 */
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private static final String PROJECTS_DIR = "projects";

    /**
     * 获取所有项目
     */
    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();

        try {
            Path projectsPath = getProjectsPath();
            if (!Files.exists(projectsPath)) {
                logger.warn("Projects directory does not exist: {}", projectsPath);
                return projects;
            }

            try (Stream<Path> paths = Files.list(projectsPath)) {
                projects = paths
                    .filter(Files::isDirectory)
                    .map(this::loadProject)
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.error("Failed to load projects", e);
        }

        return projects;
    }

    /**
     * 获取单个项目
     */
    public Project getProject(String projectId) {
        try {
            Path projectPath = getProjectsPath().resolve(projectId);
            if (!Files.exists(projectPath) || !Files.isDirectory(projectPath)) {
                return null;
            }
            return loadProject(projectPath);
        } catch (Exception e) {
            logger.error("Failed to load project: {}", projectId, e);
            return null;
        }
    }

    /**
     * 获取项目素材
     */
    public List<Asset> getProjectAssets(String projectId) {
        List<Asset> assets = new ArrayList<>();

        try {
            Path projectPath = getProjectsPath().resolve(projectId);
            if (!Files.exists(projectPath)) {
                return assets;
            }

            // 扫描图片文件
            try (Stream<Path> paths = Files.list(projectPath)) {
                assets = paths
                    .filter(this::isImageFile)
                    .map(path -> createAssetFromFile(projectId, path))
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.error("Failed to load assets for project: {}", projectId, e);
        }

        return assets;
    }

    /**
     * 获取项目剧集
     */
    public List<Episode> getProjectEpisodes(String projectId) {
        List<Episode> episodes = new ArrayList<>();

        try {
            Path projectPath = getProjectsPath().resolve(projectId);
            if (!Files.exists(projectPath)) {
                return episodes;
            }

            // 查找分镜脚本文件 (E01_分镜.md, E02_分镜.md, etc.)
            try (Stream<Path> paths = Files.list(projectPath)) {
                episodes = paths
                    .filter(path -> path.toString().matches(".*E\\d+_分镜\\.md$"))
                    .map(path -> createEpisodeFromFile(projectId, path))
                    .sorted((e1, e2) -> e1.getEpisodeNumber() - e2.getEpisodeNumber())
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.error("Failed to load episodes for project: {}", projectId, e);
        }

        return episodes;
    }

    /**
     * 加载项目信息
     */
    private Project loadProject(Path projectPath) {
        Project project = new Project();
        String projectId = projectPath.getFileName().toString();
        project.setId(projectId);
        project.setTitle(projectId);
        project.setDescription("");
        project.setStatus("in_progress");

        try {
            // 获取目录最后修改时间
            LocalDateTime modifiedTime = LocalDateTime.ofInstant(
                Files.getLastModifiedTime(projectPath).toInstant(),
                ZoneId.systemDefault()
            );
            project.setCreatedAt(modifiedTime);
            project.setUpdatedAt(modifiedTime);

            // 查找素材清单文件
            Path materialFile = findMaterialFile(projectPath);
            if (materialFile != null && Files.exists(materialFile)) {
                String content = Files.readString(materialFile);
                // 尝试从文件内容提取标题
                String title = extractTitleFromContent(content);
                if (title != null && !title.isEmpty()) {
                    project.setTitle(title);
                }
            }

            // 加载剧集
            List<Episode> episodes = getProjectEpisodes(projectId);
            project.setEpisodes(episodes);
            project.setTotalEpisodes(episodes.size());
            project.setCompletedEpisodes((int) episodes.stream()
                .filter(e -> "completed".equals(e.getStatus()))
                .count());

        } catch (IOException e) {
            logger.error("Failed to load project details: {}", projectId, e);
        }

        return project;
    }

    /**
     * 查找素材清单文件
     */
    private Path findMaterialFile(Path projectPath) throws IOException {
        try (Stream<Path> paths = Files.list(projectPath)) {
            return paths
                .filter(path -> path.toString().endsWith("_素材清单.md"))
                .findFirst()
                .orElse(null);
        }
    }

    /**
     * 从内容提取标题
     */
    private String extractTitleFromContent(String content) {
        if (content == null) return null;
        String[] lines = content.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("# ")) {
                return line.substring(2).trim();
            }
        }
        return null;
    }

    /**
     * 从文件创建素材对象
     */
    private Asset createAssetFromFile(String projectId, Path path) {
        Asset asset = new Asset();
        String filename = path.getFileName().toString();

        // 解析文件名，如 "C01 — 大师姐·正面半身.png"
        asset.setId(projectId + "/" + filename);
        asset.setProjectId(projectId);
        asset.setName(filename);

        // 判断类型
        if (filename.startsWith("C")) {
            asset.setType("character");
            asset.setCode(extractCode(filename, "C"));
        } else if (filename.startsWith("S")) {
            asset.setType("scene");
            asset.setCode(extractCode(filename, "S"));
        } else if (filename.startsWith("P")) {
            asset.setType("prop");
            asset.setCode(extractCode(filename, "P"));
        } else {
            asset.setType("other");
        }

        // 提取描述
        asset.setDescription(extractDescription(filename));

        // 设置图片 URL（相对路径）
        asset.setImageUrl("/api/projects/" + projectId + "/assets/" + filename + "/image");

        try {
            LocalDateTime modifiedTime = LocalDateTime.ofInstant(
                Files.getLastModifiedTime(path).toInstant(),
                ZoneId.systemDefault()
            );
            asset.setCreatedAt(modifiedTime);
            asset.setUpdatedAt(modifiedTime);
        } catch (IOException e) {
            asset.setCreatedAt(LocalDateTime.now());
            asset.setUpdatedAt(LocalDateTime.now());
        }

        return asset;
    }

    /**
     * 从文件创建剧集对象
     */
    private Episode createEpisodeFromFile(String projectId, Path path) {
        Episode episode = new Episode();
        String filename = path.getFileName().toString();

        // 解析剧集编号，如 "E01_分镜.md" -> 1
        int episodeNumber = extractEpisodeNumber(filename);
        episode.setId(projectId + "/E" + String.format("%02d", episodeNumber));
        episode.setProjectId(projectId);
        episode.setEpisodeNumber(episodeNumber);
        episode.setTitle("第" + episodeNumber + "集");
        episode.setStatus("draft");

        try {
            // 读取分镜内容
            String content = Files.readString(path);
            episode.setStoryboardContent(content);

            LocalDateTime modifiedTime = LocalDateTime.ofInstant(
                Files.getLastModifiedTime(path).toInstant(),
                ZoneId.systemDefault()
            );
            episode.setCreatedAt(modifiedTime);
            episode.setUpdatedAt(modifiedTime);
        } catch (IOException e) {
            episode.setCreatedAt(LocalDateTime.now());
            episode.setUpdatedAt(LocalDateTime.now());
        }

        return episode;
    }

    /**
     * 提取素材编号
     */
    private String extractCode(String filename, String prefix) {
        // C01 — xxx.png -> C01
        int endIndex = filename.indexOf(" ");
        if (endIndex == -1) {
            endIndex = filename.indexOf(".");
        }
        if (endIndex == -1) {
            return prefix + "00";
        }
        String code = filename.substring(0, endIndex);
        return code.startsWith(prefix) ? code : prefix + "00";
    }

    /**
     * 从文件名提取描述
     */
    private String extractDescription(String filename) {
        // "C01 — 大师姐·正面半身.png" -> "大师姐·正面半身"
        int start = filename.indexOf(" — ");
        if (start == -1) start = filename.indexOf(" - ");
        if (start == -1) return filename;

        int end = filename.lastIndexOf(".");
        if (end == -1) end = filename.length();

        return filename.substring(start + 3, end).trim();
    }

    /**
     * 提取剧集编号
     */
    private int extractEpisodeNumber(String filename) {
        // E01_分镜.md -> 1
        try {
            int start = filename.indexOf("E");
            if (start == -1) return 0;

            int end = filename.indexOf("_", start);
            if (end == -1) end = filename.indexOf(".", start);
            if (end == -1) return 0;

            String number = filename.substring(start + 1, end);
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(Path path) {
        String filename = path.toString().toLowerCase();
        return filename.endsWith(".png") ||
               filename.endsWith(".jpg") ||
               filename.endsWith(".jpeg") ||
               filename.endsWith(".gif") ||
               filename.endsWith(".webp");
    }

    /**
     * 获取 projects 目录路径
     */
    private Path getProjectsPath() {
        // 先尝试相对于工作目录
        Path path = Paths.get(PROJECTS_DIR);
        if (Files.exists(path)) {
            return path.toAbsolutePath().normalize();
        }

        // 尝试相对于 backend 目录的上级
        path = Paths.get("..", PROJECTS_DIR);
        if (Files.exists(path)) {
            return path.toAbsolutePath().normalize();
        }

        // 使用当前目录
        return Paths.get(PROJECTS_DIR).toAbsolutePath().normalize();
    }
}
