package dev.leonardpark.poker.model.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameStatus {
  private Player[] players = new Player[4];
  private String previousPlayer = null;
  private PlayedCards previousPlayedCards = null;
  private String currentPlayer = null;
}
