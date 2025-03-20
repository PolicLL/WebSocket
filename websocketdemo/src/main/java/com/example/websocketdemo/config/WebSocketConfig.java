package com.example.websocketdemo.config;

import com.example.websocketdemo.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHandlerMapping;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Bean
  public WebSocketHandler webSocketHandler() {
    return new MyWebSocketHandler();
  }

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
            return true;  // Allow the handshake
          }

          @Override
          public void afterHandshake(
              org.springframework.http.server.ServerHttpRequest request,
              org.springframework.http.server.ServerHttpResponse response,
              org.springframework.web.socket.WebSocketHandler wsHandler,
              Exception exception) {
            // Post-handshake processing (if any needed)
          }
        });
  }

  // Example custom WebSocketHandler implementation
  public static class MyWebSocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      // Handle WebSocket connection established
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      // Handle WebSocket messages
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
      // Handle WebSocket transport errors
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
      // Handle WebSocket connection closed
    }

    @Override
    public boolean supportsPartialMessages() {
      return false;  // Can return true if supporting partial messages
    }
  }
}
