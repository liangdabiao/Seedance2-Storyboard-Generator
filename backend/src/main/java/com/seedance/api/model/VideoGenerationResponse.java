package com.seedance.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * 视频生成响应 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoGenerationResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("error")
    private Error error;
    
    @JsonProperty("input")
    private VideoGenerationRequest.Input input;
    
    @JsonProperty("output")
    private Output output;
    
    @JsonProperty("created_at")
    private Long createdAt;
    
    @JsonProperty("modified_at")
    private Long modifiedAt;
    
    public VideoGenerationResponse() {
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Error getError() {
        return error;
    }
    
    public void setError(Error error) {
        this.error = error;
    }
    
    public VideoGenerationRequest.Input getInput() {
        return input;
    }
    
    public void setInput(VideoGenerationRequest.Input input) {
        this.input = input;
    }
    
    public Output getOutput() {
        return output;
    }
    
    public void setOutput(Output output) {
        this.output = output;
    }
    
    public Long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getModifiedAt() {
        return modifiedAt;
    }
    
    public void setModifiedAt(Long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return error == null && ("succeeded".equals(status) || "processing".equals(status) || "pending".equals(status));
    }
    
    /**
     * 判断是否完成
     */
    public boolean isCompleted() {
        return "succeeded".equals(status) || "failed".equals(status) || "cancelled".equals(status);
    }
    
    @Override
    public String toString() {
        return "VideoGenerationResponse{" +
                "id='" + id + '\'' +
                ", model='" + model + '\'' +
                ", status='" + status + '\'' +
                ", error=" + error +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
    
    /**
     * 输出内容
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Output {
        
        @JsonProperty("videos")
        private List<Video> videos;
        
        @JsonProperty("finish_reason")
        private String finishReason;
        
        @JsonProperty("usage")
        private Usage usage;
        
        // Getters and Setters
        public List<Video> getVideos() {
            return videos;
        }
        
        public void setVideos(List<Video> videos) {
            this.videos = videos;
        }
        
        public String getFinishReason() {
            return finishReason;
        }
        
        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }
        
        public Usage getUsage() {
            return usage;
        }
        
        public void setUsage(Usage usage) {
            this.usage = usage;
        }
    }
    
    /**
     * 视频信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Video {
        
        @JsonProperty("url")
        private String url;
        
        @JsonProperty("cover_url")
        private String coverUrl;
        
        @JsonProperty("duration")
        private Integer duration;
        
        @JsonProperty("fps")
        private Integer fps;
        
        @JsonProperty("resolution")
        private VideoGenerationRequest.Resolution resolution;
        
        // Getters and Setters
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getCoverUrl() {
            return coverUrl;
        }
        
        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }
        
        public Integer getDuration() {
            return duration;
        }
        
        public void setDuration(Integer duration) {
            this.duration = duration;
        }
        
        public Integer getFps() {
            return fps;
        }
        
        public void setFps(Integer fps) {
            this.fps = fps;
        }
        
        public VideoGenerationRequest.Resolution getResolution() {
            return resolution;
        }
        
        public void setResolution(VideoGenerationRequest.Resolution resolution) {
            this.resolution = resolution;
        }
    }
    
    /**
     * 用量统计
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Usage {
        
        @JsonProperty("video_tokens")
        private Integer videoTokens;
        
        // Getters and Setters
        public Integer getVideoTokens() {
            return videoTokens;
        }
        
        public void setVideoTokens(Integer videoTokens) {
            this.videoTokens = videoTokens;
        }
    }
    
    /**
     * 错误信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        
        @JsonProperty("code")
        private String code;
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("param")
        private String param;
        
        @JsonProperty("type")
        private String type;
        
        // Getters and Setters
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getParam() {
            return param;
        }
        
        public void setParam(String param) {
            this.param = param;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        @Override
        public String toString() {
            return "Error{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}
