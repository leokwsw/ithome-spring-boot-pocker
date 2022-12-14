package dev.leonardpark.poker.component;

import dev.leonardpark.poker.service.impl.GameTimer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GameTimerList {
  Map<String, GameTimer> timerList = new HashMap<>();

  public GameTimer get(String roomId) {
    return this.timerList.get(roomId);
  }

  public void add(String roomId, GameTimer timer) {
    this.timerList.put(roomId, timer);
  }

  public void remove(String roomId) {
    this.timerList.remove(roomId);
  }
}
