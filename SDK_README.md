# Seedance 视频生成 API Java SDK

基于火山引擎方舟大模型服务平台的 Seedance 视频生成 API Java 客户端。

## 快速开始

### 1. 配置

编辑 `src/main/resources/application.yaml`：

```yaml
volcengine:
  base-url: https://ark.cn-beijing.volces.com
  api-key: your-api-key-here
  endpoint: ep-your-endpoint-id
```

### 2. 简单使用

```java
// 从配置文件加载
ApiConfig config = ApiConfig.fromConfigFile("application.yaml");

// 使用服务层（推荐）
VideoGenerationService service = new VideoGenerationService(config);
String videoUrl = service.generateVideo("水墨武侠风格，剑客站在山巅");
```

## 功能特性

- ✅ 文生视频（Text-to-Video）
- ✅ 图生视频（Image-to-Video）
- ✅ 视频延长（Video Extension）
- ✅ 任务状态查询
- ✅ 批量生成
- ✅ 异步轮询等待

## 项目结构

```
src/main/java/com/seedance/
├── api/
│   ├── client/          # HTTP 客户端
│   │   ├── HttpClient.java
│   │   └── VideoGenerationClient.java
│   ├── config/          # 配置类
│   │   └── ApiConfig.java
│   ├── model/           # DTO 模型
│   │   ├── VideoGenerationRequest.java
│   │   ├── VideoGenerationResponse.java
│   │   └── VideoTaskListResponse.java
│   ├── service/         # 服务层
│   │   └── VideoGenerationService.java
│   └── util/            # 工具类
│       └── JsonUtils.java
└── example/             # 示例代码
    ├── VideoGenerationExample.java
    └── TaskManagementExample.java
```

## API 方法

### VideoGenerationClient

- `createVideoGenerationTask(request)` - 创建视频生成任务
- `getVideoGenerationTask(taskId)` - 查询任务状态
- `listVideoGenerationTasks(limit, after)` - 获取任务列表
- `cancelVideoGenerationTask(taskId)` - 取消任务
- `deleteVideoGenerationTask(taskId)` - 删除任务
- `waitForCompletion(taskId, interval, maxAttempts)` - 等待任务完成

### VideoGenerationService

- `generateVideo(prompt)` - 生成视频（简单）
- `generateVideo(prompt, duration)` - 生成视频（指定时长）
- `generateVideoFromImage(prompt, imageUrl, duration)` - 图生视频
- `batchGenerateVideos(prompts, duration)` - 批量生成
- `createVideoTask(request)` - 创建任务（不等待）

## 编译运行

```bash
# 编译
mvn clean compile

# 运行示例
mvn exec:java -Dexec.mainClass="com.seedance.example.VideoGenerationExample"

# 打包
mvn clean package
```

## 依赖

- Java 11+
- Maven 3.6+

## 注意事项

1. API Key 请妥善保管，不要提交到代码仓库
2. 视频生成是异步任务，需要轮询查询状态
3. 默认轮询间隔 5 秒，最大等待 5 分钟
4. 图生视频最多支持 9 张图片参考

## 参考文档

- [火山引擎方舟大模型服务平台](https://www.volcengine.com/docs/82379)
- [视频生成 API 文档](https://www.volcengine.com/docs/82379/1520757)
