package com.seedance.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * 视频生成请求 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoGenerationRequest {
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("input")
    private Input input;
    
    @JsonProperty("parameters")
    private Parameters parameters;
    
    public VideoGenerationRequest() {
    }
    
    public VideoGenerationRequest(String model) {
        this.model = model;
    }
    
    // Getters and Setters
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Input getInput() {
        return input;
    }
    
    public void setInput(Input input) {
        this.input = input;
    }
    
    public Parameters getParameters() {
        return parameters;
    }
    
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
    
    // Builder 模式
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private VideoGenerationRequest request = new VideoGenerationRequest();
        
        public Builder model(String model) {
            request.setModel(model);
            return this;
        }
        
        public Builder input(Input input) {
            request.setInput(input);
            return this;
        }
        
        public Builder prompt(String prompt) {
            if (request.input == null) {
                request.input = new Input();
            }
            request.input.setPrompt(prompt);
            return this;
        }
        
        public Builder negativePrompt(String negativePrompt) {
            if (request.input == null) {
                request.input = new Input();
            }
            request.input.setNegativePrompt(negativePrompt);
            return this;
        }
        
        public Builder images(List<String> images) {
            if (request.input == null) {
                request.input = new Input();
            }
            request.input.setImages(images);
            return this;
        }
        
        public Builder videos(List<String> videos) {
            if (request.input == null) {
                request.input = new Input();
            }
            request.input.setVideos(videos);
            return this;
        }
        
        public Builder parameters(Parameters parameters) {
            request.setParameters(parameters);
            return this;
        }
        
        public Builder resolution(String width, String height) {
            if (request.parameters == null) {
                request.parameters = new Parameters();
            }
            request.parameters.setResolution(width, height);
            return this;
        }
        
        public Builder duration(int duration) {
            if (request.parameters == null) {
                request.parameters = new Parameters();
            }
            request.parameters.setDuration(duration);
            return this;
        }
        
        public Builder fps(int fps) {
            if (request.parameters == null) {
                request.parameters = new Parameters();
            }
            request.parameters.setFps(fps);
            return this;
        }
        
        public Builder seed(Integer seed) {
            if (request.parameters == null) {
                request.parameters = new Parameters();
            }
            request.parameters.setSeed(seed);
            return this;
        }
        
        public VideoGenerationRequest build() {
            return request;
        }
    }
    
    /**
     * 输入内容
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Input {
        
        @JsonProperty("prompt")
        private String prompt;
        
        @JsonProperty("negative_prompt")
        private String negativePrompt;
        
        @JsonProperty("images")
        private List<String> images;
        
        @JsonProperty("videos")
        private List<String> videos;
        
        // Getters and Setters
        public String getPrompt() {
            return prompt;
        }
        
        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
        
        public String getNegativePrompt() {
            return negativePrompt;
        }
        
        public void setNegativePrompt(String negativePrompt) {
            this.negativePrompt = negativePrompt;
        }
        
        public List<String> getImages() {
            return images;
        }
        
        public void setImages(List<String> images) {
            this.images = images;
        }
        
        public List<String> getVideos() {
            return videos;
        }
        
        public void setVideos(List<String> videos) {
            this.videos = videos;
        }
    }
    
    /**
     * 生成参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Parameters {
        
        @JsonProperty("resolution")
        private Resolution resolution;
        
        @JsonProperty("duration")
        private Integer duration;
        
        @JsonProperty("fps")
        private Integer fps;
        
        @JsonProperty("seed")
        private Integer seed;
        
        // Getters and Setters
        public Resolution getResolution() {
            return resolution;
        }
        
        public void setResolution(String width, String height) {
            this.resolution = new Resolution(width, height);
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
        
        public Integer getSeed() {
            return seed;
        }
        
        public void setSeed(Integer seed) {
            this.seed = seed;
        }
    }
    
    /**
     * 分辨率设置
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Resolution {
        
        @JsonProperty("width")
        private String width;
        
        @JsonProperty("height")
        private String height;
        
        public Resolution() {
        }
        
        public Resolution(String width, String height) {
            this.width = width;
            this.height = height;
        }
        
        // Getters and Setters
        public String getWidth() {
            return width;
        }
        
        public void setWidth(String width) {
            this.width = width;
        }
        
        public String getHeight() {
            return height;
        }
        
        public void setHeight(String height) {
            this.height = height;
        }
    }
}
