package dev.leonardpark.poker.controller;

import dev.leonardpark.poker.component.Game;
import dev.leonardpark.poker.component.GameTimerList;
import dev.leonardpark.poker.component.UserStatus;
import dev.leonardpark.poker.model.game.PlayedCards;
import dev.leonardpark.poker.model.game.Player;
import dev.leonardpark.poker.model.game.PlayerActionMessage;
import dev.leonardpark.poker.service.GameService;
import dev.leonardpark.poker.service.impl.GameTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GameWsController {
  @Autowired
  private UserStatus userStatus;

  @Autowired
  private GameService gameService;

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  @Autowired
  private Game gameStatus;

  @Autowired
  private GameTimerList gameTimerList;

  @MessageMapping("/hands-info")
  public void getHandsInfo(Principal principal) {
    String name = principal.getName();
    String roomId = userStatus.getUserRoomId(name);
    this.gameService.sendHandsInfo(roomId);
  }

  @MessageMapping("/my-hands")
  public void getMyHands(Principal principal) {
    String name = principal.getName();
    this.gameService.sendMyHands(name);
  }

  @MessageMapping("/play")
  public void playCards(PlayerActionMessage playerActionMessage, Principal principal) {
    String playerName = principal.getName();
    String roomId = userStatus.getUserRoomId(playerName);
    Player player = this.gameService.findPlayer(roomId, playerName);
    Map<String, Object> response = new HashMap<>();
    GameTimer timer = gameTimerList.get(roomId);
    String action = playerActionMessage.getAction();
    if (action.equals("play")) {
      PlayedCards currentPlayedCards = playerActionMessage.getPlayedCards();
      PlayedCards previousPlayedCards = this.gameStatus.getPreviousPlayedCards(roomId);

      // 檢驗它
      if (!currentPlayedCards.isValid() || !currentPlayedCards.canBePlayedOn(previousPlayedCards)) {
        response.put("status", "fail");
        simpMessagingTemplate.convertAndSendToUser(playerName, "/queue/play", response);
        return;
      }

      player.play(currentPlayedCards.get());
      this.gameStatus.setPreviousPlayer(roomId, playerName);
      this.gameStatus.setPreviousPlayedCards(roomId, currentPlayedCards);
    }
    response.put("message", "success");
    simpMessagingTemplate.convertAndSendToUser(playerName, "/queue/play", response);
    timer.stop();
  }
}
