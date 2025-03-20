package com.example.websocketdemo.config;

import com.example.websocketdemo.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new ChatWebSocketHandler(), "/chat/{username}")
        .setAllowedOrigins("*")  // Allow all origins
        .addInterceptors(new HandshakeInterceptor() {
          @Override
          public boolean beforeHandshake(
              org.springframework.http.server.ServerHttpRequest request,
              org.springframework.http.server.ServerHttpResponse response,
              org.springframework.web.socket.WebSocketHandler wsHandler,
              java.util.Map<String, Object> attributes) throws Exception {
            return true;
          }

          @Override
          public void afterHandshake(
              org.springframework.http.server.ServerHttpRequest request,
              org.springframework.http.server.ServerHttpResponse response,
              org.springframework.web.socket.WebSocketHandler wsHandler,
              Exception exception) {
          }
        });
  }
}
