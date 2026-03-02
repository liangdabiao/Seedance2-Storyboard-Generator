package com.seedance.api.controller;

import com.seedance.api.config.VolcengineProperties;
import com.seedance.api.model.VideoGenerationRequest;
import com.seedance.api.model.VideoGenerationResponse;
import com.seedance.api.service.VideoGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频生成 REST API 控制器
 */
@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final VideoGenerationService videoService;
    private final VolcengineProperties properties;

    public VideoController(VideoGenerationService videoService, VolcengineProperties properties) {
        this.videoService = videoService;
        this.properties = properties;
    }

    /**
     * 生成视频（文生视频）
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateVideo(@RequestBody VideoGenerationRequest request) {
        try {
            if (!properties.isValid()) {
                return ResponseEntity.badRequest().body(createErrorResponse("API 配置不完整，请先配置 API Key"));
            }

            String videoUrl = videoService.generateVideo(request);
            return ResponseEntity.ok(createSuccessResponse("视频生成成功", Map.of("videoUrl", videoUrl)));
        } catch (Exception e) {
            logger.error("视频生成失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("视频生成失败: " + e.getMessage()));
        }
    }

    /**
     * 创建视频任务（异步）
     */
    @PostMapping("/tasks")
    public ResponseEntity<?> createVideoTask(@RequestBody VideoGenerationRequest request) {
        try {
            if (!properties.isValid()) {
                return ResponseEntity.badRequest().body(createErrorResponse("API 配置不完整，请先配置 API Key"));
            }

            String taskId = videoService.createVideoTask(request);
            return ResponseEntity.ok(createSuccessResponse("任务创建成功", Map.of("taskId", taskId)));
        } catch (Exception e) {
            logger.error("创建任务失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("创建任务失败: " + e.getMessage()));
        }
    }

    /**
     * 查询任务状态
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<?> getTaskStatus(@PathVariable String taskId) {
        try {
            if (!properties.isValid()) {
                return ResponseEntity.badRequest().body(createErrorResponse("API 配置不完整"));
            }

            VideoGenerationResponse response = videoService.getTaskStatus(taskId);
            return ResponseEntity.ok(createSuccessResponse("查询成功", response));
        } catch (Exception e) {
            logger.error("查询任务状态失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 取消任务
     */
    @PostMapping("/tasks/{taskId}/cancel")
    public ResponseEntity<?> cancelTask(@PathVariable String taskId) {
        try {
            if (!properties.isValid()) {
                return ResponseEntity.badRequest().body(createErrorResponse("API 配置不完整"));
            }

            videoService.cancelTask(taskId);
            return ResponseEntity.ok(createSuccessResponse("任务已取消", Map.of("taskId", taskId)));
        } catch (Exception e) {
            logger.error("取消任务失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("取消失败: " + e.getMessage()));
        }
    }

    /**
     * 批量生成视频
     */
    @PostMapping("/batch-generate")
    public ResponseEntity<?> batchGenerateVideos(@RequestBody BatchGenerateRequest request) {
        try {
            if (!properties.isValid()) {
                return ResponseEntity.badRequest().body(createErrorResponse("API 配置不完整"));
            }

            List<String> videoUrls = videoService.batchGenerateVideos(request.getPrompts(), request.getDuration());
            return ResponseEntity.ok(createSuccessResponse("批量生成完成", Map.of("videoUrls", videoUrls)));
        } catch (Exception e) {
            logger.error("批量生成失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("批量生成失败: " + e.getMessage()));
        }
    }

    /**
     * 获取支持的分辨率列表
     */
    @GetMapping("/resolutions")
    public ResponseEntity<?> getSupportedResolutions() {
        return ResponseEntity.ok(createSuccessResponse("支持的分辨率", 
            Map.of("resolutions", VideoGenerationService.SUPPORTED_RESOLUTIONS)));
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
     * 批量生成请求 DTO
     */
    public static class BatchGenerateRequest {
        private List<String> prompts;
        private int duration = 5;

        public List<String> getPrompts() {
            return prompts;
        }

        public void setPrompts(List<String> prompts) {
            this.prompts = prompts;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
