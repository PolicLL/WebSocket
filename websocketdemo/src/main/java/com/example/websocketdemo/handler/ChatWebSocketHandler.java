package com.example.websocketdemo.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    // Handle connection establishment (e.g., add to a list of connected users)
    System.out.println("New WebSocket connection established: " + session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    // Handle incoming text messages
    String payload = message.getPayload();
    System.out.println("Message received: " + payload);
    // Optionally send a response back to the client
    session.sendMessage(new TextMessage("Received your message: " + payload));
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
    // Handle connection closure
    System.out.println("WebSocket connection closed: " + session.getId());
  }
}
