package com.seedance.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 视频生成任务列表响应
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoTaskListResponse {
    
    @JsonProperty("data")
    private List<VideoGenerationResponse> data;
    
    @JsonProperty("has_more")
    private Boolean hasMore;
    
    @JsonProperty("last_id")
    private String lastId;
    
    @JsonProperty("first_id")
    private String firstId;
    
    public VideoTaskListResponse() {
    }
    
    // Getters and Setters
    public List<VideoGenerationResponse> getData() {
        return data;
    }
    
    public void setData(List<VideoGenerationResponse> data) {
        this.data = data;
    }
    
    public Boolean getHasMore() {
        return hasMore;
    }
    
    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
    
    public String getLastId() {
        return lastId;
    }
    
    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
    
    public String getFirstId() {
        return firstId;
    }
    
    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }
}
