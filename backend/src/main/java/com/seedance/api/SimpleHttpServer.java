package com.seedance.api;

import com.seedance.api.controller.ConfigController;
import com.seedance.api.util.JsonUtils;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 简单的 HTTP 服务器
 * 提供 REST API 接口
 */
public class SimpleHttpServer {
    
    private static final int PORT = 8080;
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // 配置管理 API
        server.createContext("/api/config", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                
                // 添加 CORS 头
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                
                if ("OPTIONS".equals(method)) {
                    exchange.sendResponseHeaders(200, -1);
                    return;
                }
                
                String response = "";
                
                if ("GET".equals(method)) {
                    response = ConfigController.getConfig();
                } else if ("POST".equals(method)) {
                    String requestBody = readRequestBody(exchange);
                    response = ConfigController.updateConfig(requestBody);
                }
                
                sendResponse(exchange, response);
            }
        });
        
        // 测试连接 API
        server.createContext("/api/config/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(200, -1);
                    return;
                }
                
                String response = ConfigController.testConnection();
                sendResponse(exchange, response);
            }
        });
        
        // 健康检查
        server.createContext("/api/health", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                
                String response = JsonUtils.toJson(new java.util.HashMap<String, Object>() {{
                    put("status", "ok");
                    put("timestamp", System.currentTimeMillis());
                }});
                
                sendResponse(exchange, response);
            }
        });
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("服务器已启动，监听端口: " + PORT);
        System.out.println("API 地址: http://localhost:" + PORT + "/api");
    }
    
    /**
     * 读取请求体
     */
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * 发送响应
     */
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
