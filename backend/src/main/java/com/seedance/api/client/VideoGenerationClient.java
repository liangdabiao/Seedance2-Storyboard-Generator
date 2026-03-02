package com.seedance.api.client;

import com.seedance.api.config.ApiConfig;
import com.seedance.api.model.VideoGenerationRequest;
import com.seedance.api.model.VideoGenerationResponse;
import com.seedance.api.model.VideoTaskListResponse;
import com.seedance.api.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Seedance 视频生成 API 客户端
 */
public class VideoGenerationClient {
    
    private static final Logger logger = LoggerFactory.getLogger(VideoGenerationClient.class);
    
    private final HttpClient httpClient;
    private final ApiConfig config;
    
    public VideoGenerationClient(ApiConfig config) {
        this.config = config;
        this.httpClient = new HttpClient(config);
    }
    
    public VideoGenerationClient(ApiConfig config, HttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }
    
    /**
     * 创建视频生成任务
     * 
     * @param request 视频生成请求
     * @return 视频生成响应（包含任务 ID）
     */
    public VideoGenerationResponse createVideoGenerationTask(VideoGenerationRequest request) {
        logger.info("创建视频生成任务");
        
        String responseBody = httpClient.post(config.getVideoGenerationPath(), request);
        return JsonUtils.fromJson(responseBody, VideoGenerationResponse.class);
    }
    
    /**
     * 快速创建视频生成任务（使用提示词）
     * 
     * @param prompt 视频生成提示词
     * @param duration 视频时长（秒）
     * @return 视频生成响应
     */
    public VideoGenerationResponse createVideoGenerationTask(String prompt, int duration) {
        VideoGenerationRequest request = VideoGenerationRequest.builder()
            .model(config.getModelEndpoint())
            .prompt(prompt)
            .duration(duration)
            .build();
        
        return createVideoGenerationTask(request);
    }
    
    /**
     * 查询视频生成任务状态
     * 
     * @param taskId 任务 ID
     * @return 视频生成响应
     */
    public VideoGenerationResponse getVideoGenerationTask(String taskId) {
        logger.info("查询视频生成任务状态: {}", taskId);
        
        String responseBody = httpClient.get(config.getVideoTaskQueryPath(taskId));
        return JsonUtils.fromJson(responseBody, VideoGenerationResponse.class);
    }
    
    /**
     * 获取视频生成任务列表
     * 
     * @param limit 返回数量限制
     * @param after 分页游标
     * @return 任务列表
     */
    public VideoTaskListResponse listVideoGenerationTasks(Integer limit, String after) {
        logger.info("获取视频生成任务列表");
        
        Map<String, String> params = new HashMap<>();
        if (limit != null) {
            params.put("limit", limit.toString());
        }
        if (after != null) {
            params.put("after", after);
        }
        
        String responseBody = httpClient.get(config.getVideoTaskListPath(), params);
        return JsonUtils.fromJson(responseBody, VideoTaskListResponse.class);
    }
    
    /**
     * 获取视频生成任务列表（默认）
     */
    public VideoTaskListResponse listVideoGenerationTasks() {
        return listVideoGenerationTasks(null, null);
    }
    
    /**
     * 取消视频生成任务
     * 
     * @param taskId 任务 ID
     * @return 视频生成响应
     */
    public VideoGenerationResponse cancelVideoGenerationTask(String taskId) {
        logger.info("取消视频生成任务: {}", taskId);
        
        String responseBody = httpClient.post(config.getVideoTaskCancelPath(taskId));
        return JsonUtils.fromJson(responseBody, VideoGenerationResponse.class);
    }
    
    /**
     * 删除视频生成任务
     * 
     * @param taskId 任务 ID
     */
    public void deleteVideoGenerationTask(String taskId) {
        logger.info("删除视频生成任务: {}", taskId);
        httpClient.delete(config.getVideoTaskQueryPath(taskId));
    }
    
    /**
     * 等待任务完成（轮询）
     * 
     * @param taskId 任务 ID
     * @param intervalMs 轮询间隔（毫秒）
     * @param maxAttempts 最大尝试次数
     * @return 视频生成响应
     * @throws InterruptedException 如果等待被中断
     */
    public VideoGenerationResponse waitForCompletion(String taskId, 
                                                     long intervalMs, 
                                                     int maxAttempts) 
            throws InterruptedException {
        logger.info("等待任务完成: {}, 轮询间隔: {}ms, 最大尝试次数: {}", 
                    taskId, intervalMs, maxAttempts);
        
        for (int i = 0; i < maxAttempts; i++) {
            VideoGenerationResponse response = getVideoGenerationTask(taskId);
            
            if (response.isCompleted()) {
                logger.info("任务已完成: {}, 状态: {}", taskId, response.getStatus());
                return response;
            }
            
            logger.debug("任务仍在进行中: {}, 状态: {}, 第 {}/{} 次轮询", 
                        taskId, response.getStatus(), i + 1, maxAttempts);
            
            Thread.sleep(intervalMs);
        }
        
        throw new RuntimeException("任务等待超时: " + taskId);
    }
    
    /**
     * 等待任务完成（默认配置）
     * 
     * @param taskId 任务 ID
     * @return 视频生成响应
     * @throws InterruptedException 如果等待被中断
     */
    public VideoGenerationResponse waitForCompletion(String taskId) 
            throws InterruptedException {
        // 默认每 5 秒轮询一次，最多 60 次（5 分钟）
        return waitForCompletion(taskId, 5000, 60);
    }
}
