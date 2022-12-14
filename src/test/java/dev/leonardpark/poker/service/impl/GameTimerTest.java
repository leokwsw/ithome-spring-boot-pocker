package dev.leonardpark.poker.service.impl;

import org.junit.jupiter.api.Test;

class GameTimerTest {
  @Test
  public void testGameTimer() throws InterruptedException {
    GameTimer timer = new GameTimer();
    timer.init(5);
    timer.countDown(System.out::println);
    timer.await(3);
    timer.stop();
  }
}
