package dev.leonardpark.poker.service.impl;

import dev.leonardpark.poker.component.Game;
import dev.leonardpark.poker.component.GameTimerList;
import dev.leonardpark.poker.component.RoomList;
import dev.leonardpark.poker.component.UserStatus;
import dev.leonardpark.poker.model.game.Number;
import dev.leonardpark.poker.model.game.*;
import dev.leonardpark.poker.model.room.Room;
import dev.leonardpark.poker.service.GameService;
import dev.leonardpark.poker.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class GameServiceImpl implements GameService {
  @Autowired
  private RoomList roomList;

  @Autowired
  private Game gameStatus;

  @Autowired
  private RoomService roomService;

  @Autowired
  private GameTimerList gameTimerList;

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  @Autowired
  private UserStatus userStatus;

  @Override
  public void initializeGameStatus(String roomId) {
    Room room = roomList.getRoomById(roomId);
    Deck deck = new Deck();
    deck.shuffle();
    ArrayList<ArrayList<Card>> hands = deck.deal();
    ArrayList<String> roomMembers = room.getAllMembers();
    Player[] players = new Player[4];
    for (int i = 0; i < 4; i++) {
      Player newPlayer = new Player(roomMembers.get(i));
      newPlayer.setHands(hands.get(i));
      players[i] = newPlayer;
    }

    gameStatus.add(roomId);
    gameStatus.setPlayers(roomId, players);
    gameStatus.setCurrentPlayer(roomId, this.findFirstPlayer(roomId));
  }

  @Override
  public ArrayList<Card> getHandsByPlayerName(String name) {
    return gameStatus.getHandsByPlayerName(name);
  }

  @Override
  public void sendHandsInfo(String roomId) {
    Player[] players = this.gameStatus.getPlayers(roomId);
    Map<String, Integer> handsNumber = new HashMap<>();
    for (Player player : players) {
      handsNumber.put(player.getName(), player.countHands());
    }
    this.roomService.sendMessageToRoom(roomId, "/queue/hands-info", handsNumber);
    play(roomId);
  }

  @Override
  public String findFirstPlayer(String roomId) {
    Player[] players = this.gameStatus.getPlayers(roomId);
    ArrayList<Card> hands;
    for (Player player : players) {
      hands = player.getHands();
      for (Card card : hands) {
        if (card.getSuit().equals(Suit.CLUB) && card.getNumber().equals(Number.THREE)) {
          return player.getName();
        }
      }
    }
    return null;
  }

  @Override
  public void play(String roomId) {
    Player[] players = gameStatus.getPlayers(roomId);
    int index = 0;
    for (int i = 0; i < 4; i++) {
      if (players[i].getName().equals(this.gameStatus.getCurrentPlayer(roomId))) {
        index = i;
        break;
      }
    }

    String currentPlayer;
    String previousPlayer;
    GameTimer timer = new GameTimer();
    this.gameTimerList.add(roomId, timer);
    try {
      Thread.sleep(5000);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    while (true) {
      previousPlayer = this.gameStatus.getPreviousPlayer(roomId);
      currentPlayer = players[index].getName();
      this.gameStatus.setCurrentPlayer(roomId, currentPlayer);
      if (currentPlayer.equals(previousPlayer)) {
        this.gameStatus.setPreviousPlayer(roomId, null);
        this.gameStatus.setPreviousPlayedCards(roomId, null);
      }

      timer.init(8);
      String finalCurrentPlayer = currentPlayer;
      String finalPreviousPlayer = previousPlayer;
      timer.countDown((n) -> {
        Map<String, Object> status = new HashMap<>();
        status.put("currentPlayer", finalCurrentPlayer);
        PlayedCards tmp = this.gameStatus.getPreviousPlayedCards(roomId);
        if (tmp == null) {
          status.put("previousPlayedCards", null);
        } else {
          status.put("previousPlayedCards", tmp.get());
        }
        status.put("timer", n);
        roomService.sendMessageToRoom(roomId, "/queue/game", status);

        if (n.equals("1")) {
          boolean isFirstPlayer = this.findFirstPlayer(roomId) != null;
          if (isFirstPlayer) {
            ArrayList<Card> cards = new ArrayList<>();
            cards.add(new Card(Suit.CLUB, Number.THREE));
            this.autoPlay(roomId, finalCurrentPlayer, cards);
            sendMyHands(finalCurrentPlayer);
            sendHandsInfo(roomId);
            return;
          }

          if (finalCurrentPlayer.equals(finalPreviousPlayer)) {
            this.autoPlay(roomId, finalCurrentPlayer);
            sendMyHands(finalCurrentPlayer);
            sendHandsInfo(roomId);
          }
        }
      });
      timer.await(25);

      if (gameStatus.getHandsByPlayerName(currentPlayer).size() == 0) {
        Map<String, Object> endMessage = new HashMap<>();
        endMessage.put("winner", currentPlayer);
        endMessage.put("hands", this.gameStatus.getAllPlayersHands(roomId));
        roomService.sendMessageToRoom(roomId, "/queue/end", endMessage);

        this.gameStatus.remove(roomId);
        this.gameTimerList.remove(roomId);
        for (Player player : players) {
          this.userStatus.setUserReady(player.getName(), false);
        }
        return;
      }
      index = (index + 1) % 4;
    }
  }

  @Override
  public Player findPlayer(String roomId, String name) {
    for (Player player : this.gameStatus.getPlayers(roomId))
      if (player.getName().equals(name))
        return player;
    return null;
  }

  @Override
  public void sendMyHands(String name) {
    ArrayList<Card> myHands = this.getHandsByPlayerName(name);
    this.simpMessagingTemplate.convertAndSendToUser(name, "/queue/my-hands", myHands);
  }

  public void autoPlay(String roomId, String playerName) {
    Player player = this.findPlayer(roomId, playerName);
    ArrayList<Card> cards = new ArrayList<>(player.getHands().subList(0, 1));
    player.play(cards);
    this.gameStatus.setPreviousPlayer(roomId, playerName);
    this.gameStatus.setPreviousPlayedCards(roomId, new PlayedCards(cards));
  }

  public void autoPlay(String roomId, String playerName, ArrayList<Card> cards) {
    Player player = this.findPlayer(roomId, playerName);
    player.play(cards);
    this.gameStatus.setPreviousPlayer(roomId, playerName);
    this.gameStatus.setPreviousPlayedCards(roomId, new PlayedCards(cards));
  }
}
