package com.seedance.api.service;

import com.seedance.api.client.VideoGenerationClient;
import com.seedance.api.config.ApiConfig;
import com.seedance.api.model.VideoGenerationRequest;
import com.seedance.api.model.VideoGenerationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 视频生成服务
 * 提供高级业务逻辑封装
 */
public class VideoGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(VideoGenerationService.class);
    
    // 默认视频时长（秒）
    public static final int DEFAULT_DURATION = 5;
    
    // 支持的分辨率
    public static final List<String> SUPPORTED_RESOLUTIONS = Arrays.asList(
        "480x854",   // 9:16 竖屏
        "540x960",   // 9:16 竖屏
        "720x1280",  // 9:16 竖屏
        "854x480",   // 16:9 横屏
        "960x540",   // 16:9 横屏
        "1280x720"   // 16:9 横屏
    );
    
    private final VideoGenerationClient client;
    
    public VideoGenerationService(ApiConfig config) {
        this.client = new VideoGenerationClient(config);
    }
    
    public VideoGenerationService(VideoGenerationClient client) {
        this.client = client;
    }
    
    /**
     * 生成视频（简单方式）
     * 
     * @param prompt 视频描述提示词
     * @return 生成的视频 URL
     */
    public String generateVideo(String prompt) {
        return generateVideo(prompt, DEFAULT_DURATION);
    }
    
    /**
     * 生成视频（指定时长）
     * 
     * @param prompt 视频描述提示词
     * @param duration 视频时长（秒）
     * @return 生成的视频 URL
     */
    public String generateVideo(String prompt, int duration) {
        VideoGenerationRequest request = VideoGenerationRequest.builder()
            .prompt(prompt)
            .duration(duration)
            .build();
        
        return generateVideo(request);
    }
    
    /**
     * 生成视频（带图生视频）
     * 
     * @param prompt 视频描述提示词
     * @param imageUrl 参考图片 URL
     * @param duration 视频时长（秒）
     * @return 生成的视频 URL
     */
    public String generateVideoFromImage(String prompt, String imageUrl, int duration) {
        VideoGenerationRequest request = VideoGenerationRequest.builder()
            .prompt(prompt)
            .images(Arrays.asList(imageUrl))
            .duration(duration)
            .build();
        
        return generateVideo(request);
    }
    
    /**
     * 生成视频（完整参数）
     * 
     * @param request 视频生成请求
     * @return 生成的视频 URL
     */
    public String generateVideo(VideoGenerationRequest request) {
        try {
            // 1. 创建任务
            VideoGenerationResponse response = client.createVideoGenerationTask(request);
            
            if (!response.isSuccess()) {
                throw new RuntimeException("创建任务失败: " + 
                    (response.getError() != null ? response.getError().getMessage() : "未知错误"));
            }
            
            String taskId = response.getId();
            logger.info("任务已创建: {}", taskId);
            
            // 2. 等待任务完成
            VideoGenerationResponse completedResponse = client.waitForCompletion(taskId);
            
            if (!"succeeded".equals(completedResponse.getStatus())) {
                throw new RuntimeException("视频生成失败: " + completedResponse.getStatus());
            }
            
            // 3. 返回视频 URL
            if (completedResponse.getOutput() != null && 
                completedResponse.getOutput().getVideos() != null &&
                !completedResponse.getOutput().getVideos().isEmpty()) {
                
                String videoUrl = completedResponse.getOutput().getVideos().get(0).getUrl();
                logger.info("视频生成成功: {}", videoUrl);
                return videoUrl;
            }
            
            throw new RuntimeException("视频生成成功但未返回视频 URL");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("等待任务完成时被中断", e);
        }
    }
    
    /**
     * 批量生成视频
     * 
     * @param prompts 提示词列表
     * @param duration 视频时长
     * @return 视频 URL 列表
     */
    public java.util.List<String> batchGenerateVideos(java.util.List<String> prompts, int duration) {
        java.util.List<String> videoUrls = new java.util.ArrayList<>();
        
        for (int i = 0; i < prompts.size(); i++) {
            logger.info("正在生成第 {}/{} 个视频", i + 1, prompts.size());
            try {
                String videoUrl = generateVideo(prompts.get(i), duration);
                videoUrls.add(videoUrl);
            } catch (Exception e) {
                logger.error("生成第 {} 个视频失败: {}", i + 1, e.getMessage());
                videoUrls.add(null);
            }
        }
        
        return videoUrls;
    }
    
    /**
     * 创建视频任务但不等待完成
     * 
     * @param request 视频生成请求
     * @return 任务 ID
     */
    public String createVideoTask(VideoGenerationRequest request) {
        VideoGenerationResponse response = client.createVideoGenerationTask(request);
        
        if (!response.isSuccess()) {
            throw new RuntimeException("创建任务失败: " + 
                (response.getError() != null ? response.getError().getMessage() : "未知错误"));
        }
        
        return response.getId();
    }
    
    /**
     * 获取任务状态
     * 
     * @param taskId 任务 ID
     * @return 任务状态
     */
    public VideoGenerationResponse getTaskStatus(String taskId) {
        return client.getVideoGenerationTask(taskId);
    }
    
    /**
     * 取消任务
     * 
     * @param taskId 任务 ID
     */
    public void cancelTask(String taskId) {
        client.cancelVideoGenerationTask(taskId);
    }
}
