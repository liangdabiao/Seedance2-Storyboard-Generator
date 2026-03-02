package com.seedance.api.controller;

import com.seedance.api.model.Asset;
import com.seedance.api.model.Episode;
import com.seedance.api.model.Project;
import com.seedance.api.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目 REST API 控制器
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 获取所有项目
     */
    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            return ResponseEntity.ok(createSuccessResponse("获取项目列表成功", projects));
        } catch (Exception e) {
            logger.error("获取项目列表失败", e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("获取项目列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取单个项目
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProject(@PathVariable String id) {
        try {
            Project project = projectService.getProject(id);
            if (project == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(createSuccessResponse("获取项目成功", project));
        } catch (Exception e) {
            logger.error("获取项目失败: {}", id, e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("获取项目失败: " + e.getMessage()));
        }
    }

    /**
     * 获取项目素材
     */
    @GetMapping("/{id}/assets")
    public ResponseEntity<?> getProjectAssets(@PathVariable String id) {
        try {
            List<Asset> assets = projectService.getProjectAssets(id);
            return ResponseEntity.ok(createSuccessResponse("获取素材列表成功", assets));
        } catch (Exception e) {
            logger.error("获取素材列表失败: {}", id, e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("获取素材列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取单个素材图片
     */
    @GetMapping("/{id}/assets/{assetName}/image")
    public ResponseEntity<Resource> getAssetImage(
            @PathVariable String id,
            @PathVariable String assetName) {
        try {
            // 从 projects 目录加载图片
            Path imagePath = Paths.get("projects", id, assetName);
            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // 判断 Content-Type
            String contentType = determineContentType(assetName);

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + assetName + "\"")
                .body(resource);

        } catch (MalformedURLException e) {
            logger.error("加载图片失败: {}/{}", id, assetName, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取项目剧集
     */
    @GetMapping("/{id}/episodes")
    public ResponseEntity<?> getProjectEpisodes(@PathVariable String id) {
        try {
            List<Episode> episodes = projectService.getProjectEpisodes(id);
            return ResponseEntity.ok(createSuccessResponse("获取剧集列表成功", episodes));
        } catch (Exception e) {
            logger.error("获取剧集列表失败: {}", id, e);
            return ResponseEntity.internalServerError()
                .body(createErrorResponse("获取剧集列表失败: " + e.getMessage()));
        }
    }

    /**
     * 创建项目
     */
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest request) {
        try {
            Project project = projectService.createProject(
                request.getId(),
                request.getTitle(),
                request.getDescription()
            );
            return ResponseEntity.ok(createSuccessResponse("项目创建成功", project));
        } catch (Exception e) {
            logger.error("创建项目失败", e);
            return ResponseEntity.badRequest()
                .body(createErrorResponse("创建项目失败: " + e.getMessage()));
        }
    }

    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable String id, @RequestBody Project project) {
        // 暂不支持更新，返回提示
        return ResponseEntity.ok(createSuccessResponse("项目更新功能待实现", project));
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok(createSuccessResponse("项目删除成功", Map.of("id", id)));
        } catch (Exception e) {
            logger.error("删除项目失败: {}", id, e);
            return ResponseEntity.badRequest()
                .body(createErrorResponse("删除项目失败: " + e.getMessage()));
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

    private String determineContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }

    /**
     * 创建项目请求 DTO
     */
    public static class CreateProjectRequest {
        private String id;
        private String title;
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
