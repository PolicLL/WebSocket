package com.example.websocketdemo.endpoint;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Ensures clean context
public class ChatEndpointTest {

  @LocalServerPort
  private int port; // Inject the random port

  private static Session session;
  private static CountDownLatch latch;
  private static String receivedMessage;

  @ClientEndpoint
  public static class WebSocketTestClient {
    @OnMessage
    public void onMessage(String message) {
      receivedMessage = message;
      latch.countDown(); // Notify that a message was received
    }
  }

  @BeforeEach
  void setup() throws Exception {
    Thread.sleep(1000); // Small delay to wait for WebSocket to be ready
    URI uri = new URI("ws://localhost:" + port + "/chat/testUser");
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    session = container.connectToServer(WebSocketTestClient.class, uri);
  }


  @Test
  void testWebSocketConnection() throws IOException, InterruptedException {
    assertNotNull(session); // Ensure connection is established
    session.getBasicRemote().sendText("{\"from\":\"testUser\",\"content\":\"Hello!\"}");

    boolean messageReceived = latch.await(3, TimeUnit.SECONDS); // Wait max 3 seconds
    assertTrue(messageReceived, "Message was not received");
    assertNotNull(receivedMessage, "Received message should not be null");
    assertTrue(receivedMessage.contains("Hello!"), "Message content is incorrect");
  }

  @AfterEach
  void tearDown() throws Exception {
    if (session != null && session.isOpen()) {
      session.close();
    }
  }
}
