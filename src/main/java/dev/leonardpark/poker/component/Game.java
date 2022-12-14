package dev.leonardpark.poker.component;


import dev.leonardpark.poker.model.game.Card;
import dev.leonardpark.poker.model.game.GameStatus;
import dev.leonardpark.poker.model.game.PlayedCards;
import dev.leonardpark.poker.model.game.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class Game {
  @Autowired
  private UserStatus userStatus;

  private final Map<String, GameStatus> game = new HashMap<>();

  public void add(String roomId) {
    this.game.put(roomId, new GameStatus());
  }

  public void remove(String roomId) {
    this.game.remove(roomId);
  }

  public void setPlayers(String roomId, Player[] players) {
    this.game.get(roomId).setPlayers(players);
  }

  public Player[] getPlayers(String roomId) {
    return this.game.get(roomId).getPlayers();
  }

  public void setPreviousPlayer(String roomId, String playerName) {
    this.game.get(roomId).setPreviousPlayer(playerName);
  }

  public String getPreviousPlayer(String roomId) {
    return this.game.get(roomId).getPreviousPlayer();
  }

  public void setPreviousPlayedCards(String roomId, PlayedCards playedCards) {
    this.game.get(roomId).setPreviousPlayedCards(playedCards);
  }

  public PlayedCards getPreviousPlayedCards(String roomId) {
    return this.game.get(roomId).getPreviousPlayedCards();
  }

  public void setCurrentPlayer(String roomId, String playerName) {
    this.game.get(roomId).setCurrentPlayer(playerName);
  }

  public String getCurrentPlayer(String roomId) {
    return this.game.get(roomId).getCurrentPlayer();
  }

  public ArrayList<Card> getHandsByPlayerName(String name) {
    String roomId = this.userStatus.getUserRoomId(name);
    Player[] players = this.getPlayers(roomId);
    for (Player player : players) {
      if (player.getName().equals(name)) {
        return player.getHands();
      }
    }
    return null;
  }

  public ArrayList<ArrayList<Card>> getAllPlayersHands(String roomId) {
    ArrayList<ArrayList<Card>> result = new ArrayList<>();
    Player[] players = this.game.get(roomId).getPlayers();
    for (Player player : players) {
      result.add(player.getHands());
    }
    return result;
  }
}
