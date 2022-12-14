package dev.leonardpark.poker.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class UserReadyResponse {
  @Getter
  private final Map<String, Boolean> userReadyStatus = new HashMap<>();

  @Getter
  @Setter
  private boolean allReady = false;

  @Getter
  @Setter
  private String message = "";

  public void add(String username, boolean isReady) {
    this.userReadyStatus.put(username, isReady);
  }
}
