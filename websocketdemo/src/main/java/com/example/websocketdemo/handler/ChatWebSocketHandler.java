package com.example.websocketdemo.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatWebSocketHandler extends TextWebSocketHandler {

  private static final Set<WebSocketSession> sessions = new HashSet<>();
  private static List<Integer> player1Numbers = new ArrayList<>();
  private static List<Integer> player2Numbers = new ArrayList<>();
  private static List<Integer> player1Choices = new ArrayList<>();
  private static List<Integer> player2Choices = new ArrayList<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessions.add(session);
    System.out.println("New client connected.");

    // Generate 4 random numbers for each player
    if (sessions.size() == 1) {
      generateNumbers(player1Numbers);
      sendNumbersToPlayer(session, player1Numbers);
      session.sendMessage(new TextMessage("You are Player 1. Please wait for Player 2 to connect."));
    } else if (sessions.size() == 2) {
      generateNumbers(player2Numbers);
      sendNumbersToPlayer(session, player2Numbers);
      sessions.iterator().next().sendMessage(new TextMessage("Player 2 has connected. You can now start choosing numbers."));
      session.sendMessage(new TextMessage("You are Player 2. Please wait for Player 1 to choose a number."));
    }

    // Once both players are connected, start the game
    if (sessions.size() == 2) {
      sessions.iterator().next().sendMessage(new TextMessage("It is Player 1's turn. Please choose a number."));
      sessions.iterator().next().sendMessage(new TextMessage("It is Player 2's turn. Please wait for Player 1 to choose a number."));
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String choice = message.getPayload().trim();

    // Handle the player's choice
    handlePlayerChoice(session, choice);
  }

  private void handlePlayerChoice(WebSocketSession session, String choice) throws IOException {
    int selectedNumber = Integer.parseInt(choice);

    if (sessions.iterator().next().equals(session)) {
      player1Choices.add(selectedNumber);
      player1Numbers.remove(Integer.valueOf(selectedNumber)); // Remove the chosen number
    } else {
      player2Choices.add(selectedNumber);
      player2Numbers.remove(Integer.valueOf(selectedNumber)); // Remove the chosen number
    }

    // Send the updated list of numbers to both players (hide the chosen numbers)
    sendNumbersToPlayer(session, getPlayerNumbers(session));

    // If both players have selected 4 numbers, announce the winner and reset for next round
    if (player1Choices.size() == 4 && player2Choices.size() == 4) {
      // Compare the chosen numbers and declare the winner
      String result = compareChoices();
      for (WebSocketSession ws : sessions) {
        ws.sendMessage(new TextMessage(result));
      }

      // Reset for next round
      resetGame();
    } else {
      // Otherwise, send the next prompt
      sendNextTurnMessage();
    }
  }

  private void sendNumbersToPlayer(WebSocketSession session, List<Integer> numbers) throws IOException {
    String message = "Your numbers: " + numbers.toString();
    session.sendMessage(new TextMessage(message));
  }

  private List<Integer> getPlayerNumbers(WebSocketSession session) {
    return sessions.iterator().next().equals(session) ? player1Numbers : player2Numbers;
  }

  private String compareChoices() {
    int player1Total = player1Choices.stream().mapToInt(Integer::intValue).sum();
    int player2Total = player2Choices.stream().mapToInt(Integer::intValue).sum();

    if (player1Total > player2Total) {
      return "Player 1 wins!";
    } else if (player2Total > player1Total) {
      return "Player 2 wins!";
    } else {
      return "It's a tie!";
    }
  }

  private void resetGame() {
    // Reset the lists for the next round
    player1Choices.clear();
    player2Choices.clear();
    generateNumbers(player1Numbers);
    generateNumbers(player2Numbers);

    // Announce the start of the next round
    for (WebSocketSession ws : sessions) {
      try {
        ws.sendMessage(new TextMessage("Starting a new round! Choose a number."));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendNextTurnMessage() throws IOException {
    if (player1Choices.size() < 4) {
      sessions.iterator().next().sendMessage(new TextMessage("It is Player 1's turn. Please choose a number."));
      sessions.iterator().next().sendMessage(new TextMessage("Waiting for Player 1 to choose a number."));
    } else if (player2Choices.size() < 4) {
      sessions.iterator().next().sendMessage(new TextMessage("It is Player 2's turn. Please choose a number."));
      sessions.iterator().next().sendMessage(new TextMessage("Waiting for Player 2 to choose a number."));
    }
  }

  private void generateNumbers(List<Integer> numbers) {
    // Generate 4 random numbers between 1 and 100
    numbers.clear();
    for (int i = 0; i < 4; i++) {
      numbers.add((int) (Math.random() * 100) + 1);
    }
  }
}
