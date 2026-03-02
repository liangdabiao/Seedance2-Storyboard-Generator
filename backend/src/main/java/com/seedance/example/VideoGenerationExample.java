package com.seedance.example;

import com.seedance.api.client.VideoGenerationClient;
import com.seedance.api.config.ApiConfig;
import com.seedance.api.model.VideoGenerationRequest;
import com.seedance.api.model.VideoGenerationResponse;
import com.seedance.api.service.VideoGenerationService;

import java.util.Arrays;
import java.util.List;

/**
 * 视频生成示例
 */
public class VideoGenerationExample {
    
    public static void main(String[] args) {
        // 从配置文件加载配置
        ApiConfig config = ApiConfig.fromConfigFile("application.yaml");
        
        // 或者手动创建配置
        // ApiConfig config = new ApiConfig(
        //     "https://ark.cn-beijing.volces.com",
        //     "your-api-key",
        //     "ep-your-endpoint-id"
        // );
        
        System.out.println("配置信息: " + config);
        
        // 方式 1：使用高级服务
        exampleWithService(config);
        
        // 方式 2：使用底层客户端
        // exampleWithClient(config);
    }
    
    /**
     * 使用服务层（推荐）
     */
    private static void exampleWithService(ApiConfig config) {
        VideoGenerationService service = new VideoGenerationService(config);
        
        // 示例 1：简单生成视频
        try {
            String videoUrl = service.generateVideo(
                "水墨武侠风格，一位剑客站在山巅，风吹衣袂飘动"
            );
            System.out.println("生成成功: " + videoUrl);
        } catch (Exception e) {
            System.err.println("生成失败: " + e.getMessage());
        }
        
        // 示例 2：指定时长
        // String videoUrl = service.generateVideo(
        //     "水墨武侠风格，一位剑客站在山巅", 
        //     10  // 10 秒
        // );
        
        // 示例 3：图生视频
        // String videoUrl = service.generateVideoFromImage(
        //     "让图片中的人物动起来，缓慢转身",
        //     "https://example.com/image.jpg",
        //     5
        // );
        
        // 示例 4：批量生成
        // List<String> prompts = Arrays.asList(
        //     "第一幕：剑客站在山巅",
        //     "第二幕：剑客拔出宝剑",
        //     "第三幕：剑客挥剑斩出"
        // );
        // List<String> urls = service.batchGenerateVideos(prompts, 5);
    }
    
    /**
     * 使用客户端（更灵活）
     */
    private static void exampleWithClient(ApiConfig config) {
        VideoGenerationClient client = new VideoGenerationClient(config);
        
        try {
            // 1. 创建视频生成任务
            VideoGenerationRequest request = VideoGenerationRequest.builder()
                .model(config.getModelEndpoint())
                .prompt("水墨武侠风格，一位剑客站在山巅，风吹衣袂飘动")
                .duration(5)
                .fps(24)
                .resolution("720", "1280")  // 9:16 竖屏
                .build();
            
            VideoGenerationResponse response = client.createVideoGenerationTask(request);
            System.out.println("任务创建成功，任务 ID: " + response.getId());
            System.out.println("任务状态: " + response.getStatus());
            
            // 2. 等待任务完成
            VideoGenerationResponse completed = client.waitForCompletion(response.getId());
            System.out.println("任务完成，状态: " + completed.getStatus());
            
            // 3. 获取视频 URL
            if (completed.getOutput() != null && completed.getOutput().getVideos() != null) {
                for (VideoGenerationResponse.Video video : completed.getOutput().getVideos()) {
                    System.out.println("视频 URL: " + video.getUrl());
                    System.out.println("封面 URL: " + video.getCoverUrl());
                    System.out.println("时长: " + video.getDuration() + " 秒");
                }
            }
            
        } catch (InterruptedException e) {
            System.err.println("等待被中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
    }
}
