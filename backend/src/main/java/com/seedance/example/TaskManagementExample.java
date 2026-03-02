package main.java.com.seedance.example;

import com.seedance.api.client.VideoGenerationClient;
import com.seedance.api.config.ApiConfig;
import com.seedance.api.model.VideoGenerationRequest;
import com.seedance.api.model.VideoGenerationResponse;
import com.seedance.api.model.VideoTaskListResponse;

/**
 * 任务管理示例
 */
public class TaskManagementExample {
    
    public static void main(String[] args) {
        ApiConfig config = ApiConfig.fromConfigFile("application.yaml");
        VideoGenerationClient client = new VideoGenerationClient(config);
        
        // 1. 创建任务
        String taskId = createTask(client);
        
        // 2. 查询任务状态
        queryTask(client, taskId);
        
        // 3. 列出所有任务
        listTasks(client);
        
        // 4. 取消任务（如果需要）
        // cancelTask(client, taskId);
        
        // 5. 删除任务（如果需要）
        // deleteTask(client, taskId);
    }
    
    private static String createTask(VideoGenerationClient client) {
        VideoGenerationRequest request = VideoGenerationRequest.builder()
            .model("ep-your-endpoint-id")
            .prompt("水墨画风格，山水风景")
            .duration(5)
            .build();
        
        VideoGenerationResponse response = client.createVideoGenerationTask(request);
        System.out.println("创建任务成功: " + response.getId());
        return response.getId();
    }
    
    private static void queryTask(VideoGenerationClient client, String taskId) {
        VideoGenerationResponse response = client.getVideoGenerationTask(taskId);
        System.out.println("任务 ID: " + response.getId());
        System.out.println("任务状态: " + response.getStatus());
        System.out.println("创建时间: " + response.getCreatedAt());
        
        if (response.getError() != null) {
            System.out.println("错误信息: " + response.getError().getMessage());
        }
    }
    
    private static void listTasks(VideoGenerationClient client) {
        VideoTaskListResponse response = client.listVideoGenerationTasks(10, null);
        System.out.println("任务列表:");
        if (response.getData() != null) {
            for (VideoGenerationResponse task : response.getData()) {
                System.out.println("  - ID: " + task.getId() + ", 状态: " + task.getStatus());
            }
        }
        System.out.println("是否有更多: " + response.getHasMore());
    }
    
    private static void cancelTask(VideoGenerationClient client, String taskId) {
        VideoGenerationResponse response = client.cancelVideoGenerationTask(taskId);
        System.out.println("取消任务: " + response.getId() + ", 状态: " + response.getStatus());
    }
    
    private static void deleteTask(VideoGenerationClient client, String taskId) {
        client.deleteVideoGenerationTask(taskId);
        System.out.println("删除任务: " + taskId);
    }
}
