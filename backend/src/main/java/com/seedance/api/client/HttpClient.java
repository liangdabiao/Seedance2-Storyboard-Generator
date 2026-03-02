package com.seedance.api.client;

import com.seedance.api.config.ApiConfig;
import com.seedance.api.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * HTTP 客户端
 * 用于调用火山引擎 API
 */
public class HttpClient {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    
    private static final int DEFAULT_TIMEOUT = 60000; // 60 秒
    private static final int DEFAULT_READ_TIMEOUT = 120000; // 120 秒
    
    private final ApiConfig config;
    private final int connectTimeout;
    private final int readTimeout;
    
    public HttpClient(ApiConfig config) {
        this(config, DEFAULT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    
    public HttpClient(ApiConfig config, int connectTimeout, int readTimeout) {
        this.config = config;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }
    
    /**
     * 发送 POST 请求
     */
    public String post(String path, Object body) {
        String url = config.getBaseUrl() + path;
        String jsonBody = body != null ? JsonUtils.toJson(body) : null;
        
        logger.debug("POST 请求 URL: {}", url);
        logger.debug("请求体: {}", jsonBody);
        
        try {
            HttpURLConnection connection = createConnection(url, "POST");
            
            if (jsonBody != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            return handleResponse(connection);
        } catch (Exception e) {
            logger.error("POST 请求失败: {}", e.getMessage());
            throw new RuntimeException("API 请求失败", e);
        }
    }
    
    /**
     * 发送 GET 请求
     */
    public String get(String path) {
        String url = config.getBaseUrl() + path;
        
        logger.debug("GET 请求 URL: {}", url);
        
        try {
            HttpURLConnection connection = createConnection(url, "GET");
            return handleResponse(connection);
        } catch (Exception e) {
            logger.error("GET 请求失败: {}", e.getMessage());
            throw new RuntimeException("API 请求失败", e);
        }
    }
    
    /**
     * 发送 GET 请求（带查询参数）
     */
    public String get(String path, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(config.getBaseUrl() + path);
        
        if (params != null && !params.isEmpty()) {
            urlBuilder.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }
        
        String url = urlBuilder.toString();
        logger.debug("GET 请求 URL: {}", url);
        
        try {
            HttpURLConnection connection = createConnection(url, "GET");
            return handleResponse(connection);
        } catch (Exception e) {
            logger.error("GET 请求失败: {}", e.getMessage());
            throw new RuntimeException("API 请求失败", e);
        }
    }
    
    /**
     * 发送 DELETE 请求
     */
    public String delete(String path) {
        String url = config.getBaseUrl() + path;
        
        logger.debug("DELETE 请求 URL: {}", url);
        
        try {
            HttpURLConnection connection = createConnection(url, "DELETE");
            return handleResponse(connection);
        } catch (Exception e) {
            logger.error("DELETE 请求失败: {}", e.getMessage());
            throw new RuntimeException("API 请求失败", e);
        }
    }
    
    /**
     * 发送 POST 请求（空体）
     */
    public String post(String path) {
        return post(path, null);
    }
    
    /**
     * 创建 HTTP 连接
     */
    private HttpURLConnection createConnection(String urlStr, String method) 
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // 设置请求方法
        connection.setRequestMethod(method);
        
        // 设置超时
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        
        // 设置请求头
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
        
        return connection;
    }
    
    /**
     * 处理响应
     */
    private String handleResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        logger.debug("响应状态码: {}", responseCode);
        
        InputStream inputStream;
        if (responseCode >= 200 && responseCode < 300) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }
        
        if (inputStream == null) {
            throw new RuntimeException("无法获取响应流，状态码: " + responseCode);
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            String responseBody = response.toString();
            logger.debug("响应体: {}", responseBody);
            
            if (responseCode >= 400) {
                throw new RuntimeException("API 返回错误，状态码: " + responseCode + 
                                           ", 响应: " + responseBody);
            }
            
            return responseBody;
        }
    }
    
    /**
     * 跳过 SSL 证书验证（仅用于测试环境）
     */
    public static void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            // 忽略主机名验证
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("禁用 SSL 验证失败", e);
        }
    }
}
