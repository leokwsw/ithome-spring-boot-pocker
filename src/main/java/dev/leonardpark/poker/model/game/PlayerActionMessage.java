package dev.leonardpark.poker.model.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PlayerActionMessage {
  private String action;
  private PlayedCards playedCards;

  public void setPlayedCards(ArrayList<Card> cards) {
    this.playedCards = new PlayedCards(cards);
  }
}
