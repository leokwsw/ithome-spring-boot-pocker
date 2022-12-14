package dev.leonardpark.poker.service;

import dev.leonardpark.poker.model.game.Card;
import dev.leonardpark.poker.model.game.Player;

import java.util.ArrayList;

public interface GameService {
  void initializeGameStatus(String roomId);

  ArrayList<Card> getHandsByPlayerName(String name);

  void sendHandsInfo(String roomId);

  String findFirstPlayer(String roomId);

  void play(String roomId);

  Player findPlayer(String roomId, String name);

  void sendMyHands(String name);

  void autoPlay(String roomId, String playerName);

  void autoPlay(String roomId, String playerName, ArrayList<Card> cards);
}
