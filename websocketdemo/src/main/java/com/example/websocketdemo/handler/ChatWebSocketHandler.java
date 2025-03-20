package com.example.websocketdemo.handler;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatWebSocketHandler extends TextWebSocketHandler {

  private WebSocketSession client1 = null;
  private WebSocketSession client2 = null;
  private final List<Integer> client1Numbers = new ArrayList<>();
  private final List<Integer> client2Numbers = new ArrayList<>();
  private final List<Integer> availableNumbers = new ArrayList<>();
  private final AtomicInteger turn = new AtomicInteger(0); // Tracks whose turn it is

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    if (client1 == null) {
      client1 = session;
      client1.sendMessage(new TextMessage("Waiting for second player..."));
    } else if (client2 == null) {
      client2 = session;
      client2.sendMessage(new TextMessage("Both players connected!"));
      generateNumbers();
      sendNumbersToPlayers();
    }
  }

  private void generateNumbers() {
    Random random = new Random();
    for (int i = 0; i < 8; i++) {
      availableNumbers.add(random.nextInt(100) + 1);
    }
  }

  private void sendNumbersToPlayers() throws IOException {
    List<Integer> client1PrivateNumbers = new ArrayList<>(availableNumbers.subList(0, 4));
    List<Integer> client2PrivateNumbers = new ArrayList<>(availableNumbers.subList(4, 8));

    client1.sendMessage(new TextMessage("Available numbers: [" + client1PrivateNumbers + "]"));
    client2.sendMessage(new TextMessage("Available numbers: [" + client2PrivateNumbers + "]"));

    promptPlayer(client1);
  }

  private void promptPlayer(WebSocketSession session) throws IOException {
    if (session == client1) {
      client1.sendMessage(new TextMessage("Player 1, choose a number from your list."));
    } else {
      client2.sendMessage(new TextMessage("Player 2, choose a number from your list."));
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String chosenNumberString = message.getPayload();
    int chosenNumber;
    try {
      chosenNumber = Integer.parseInt(chosenNumberString);
    } catch (NumberFormatException e) {
      session.sendMessage(new TextMessage("Invalid input. Please choose a valid number!"));
      return;
    }

    if (session == client1) {
      handlePlayerChoice(client1, chosenNumber, client1Numbers);
    } else if (session == client2) {
      handlePlayerChoice(client2, chosenNumber, client2Numbers);
    }
  }

  private void handlePlayerChoice(WebSocketSession session, int chosenNumber, List<Integer> playerNumbers) throws IOException {
    // Ensure the number is part of the player's numbers
    if (!playerNumbers.contains(chosenNumber)) {
      session.sendMessage(new TextMessage("You can only choose from your available numbers."));
      return;
    }

    // Remove the chosen number from the player's list and the available list
    playerNumbers.remove(Integer.valueOf(chosenNumber));
    availableNumbers.remove(Integer.valueOf(chosenNumber));

    // Alternate turns
    if (turn.get() % 2 == 0) {
      turn.incrementAndGet();
      promptPlayer(client2); // Prompt second player
    } else {
      turn.incrementAndGet();
      promptPlayer(client1); // Prompt first player
    }

    // Check if all numbers have been chosen
    if (client1Numbers.size() == 4 && client2Numbers.size() == 4) {
      announceWinner();
    }
  }

  private void announceWinner() throws IOException {
    int client1Total = client1Numbers.stream().mapToInt(Integer::intValue).sum();
    int client2Total = client2Numbers.stream().mapToInt(Integer::intValue).sum();

    String winnerMessage = "Game over! ";
    if (client1Total > client2Total) {
      winnerMessage += "Player 1 wins with a total of " + client1Total + "!";
    } else if (client2Total > client1Total) {
      winnerMessage += "Player 2 wins with a total of " + client2Total + "!";
    } else {
      winnerMessage += "It's a tie! Both players have the same total.";
    }

    client1.sendMessage(new TextMessage(winnerMessage));
    client2.sendMessage(new TextMessage(winnerMessage));

    // Reset the game for the next round
    resetGame();
  }

  private void resetGame() {
    availableNumbers.clear();
    client1Numbers.clear();
    client2Numbers.clear();
    turn.set(0);
    client1 = null;
    client2 = null;
  }
}
