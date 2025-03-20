package com.example.websocketdemo.handler;


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatWebSocketHandler extends TextWebSocketHandler {

  // To store clients' data
  private WebSocketSession client1 = null;
  private WebSocketSession client2 = null;
  private int client1Number = 0;
  private int client2Number = 0;
  private final AtomicInteger turn = new AtomicInteger(0); // To track whose turn it is

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    if (client1 == null) {
      client1 = session;
      session.sendMessage(new TextMessage("Waiting for second player..."));
    } else if (client2 == null) {
      client2 = session;
      session.sendMessage(new TextMessage("Both players connected!"));
      startGame();
    }
  }

  private void startGame() throws IOException {
    // Notify the first player to enter a number
    client1.sendMessage(new TextMessage("Your turn! Please enter a number:"));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    if (session == client1) {
      // First client input handling
      if (turn.get() == 0) {
        try {
          client1Number = Integer.parseInt(message.getPayload());
          turn.set(1);
          client2.sendMessage(new TextMessage("Your turn! Please enter a number:"));
        } catch (NumberFormatException e) {
          client1.sendMessage(new TextMessage("Invalid input, please enter a valid number!"));
        }
      }
    } else if (session == client2) {
      // Second client input handling
      if (turn.get() == 1) {
        try {
          client2Number = Integer.parseInt(message.getPayload());
          compareNumbers();
        } catch (NumberFormatException e) {
          client2.sendMessage(new TextMessage("Invalid input, please enter a valid number!"));
        }
      }
    }
  }

  private void compareNumbers() throws IOException {
    String result;
    if (client1Number > client2Number) {
      result = "Player 1 wins!";
    } else if (client1Number < client2Number) {
      result = "Player 2 wins!";
    } else {
      result = "It's a tie!";
    }

    // Send results to both clients
    client1.sendMessage(new TextMessage(result));
    client2.sendMessage(new TextMessage(result));
  }
}
