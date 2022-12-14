package dev.leonardpark.poker.model.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Player {
  private String name;
  private ArrayList<Card> hands;

  public Player(String name) {
    this.name = name;
  }

  public Player(String name, ArrayList<Card> hands) {
    this.name = name;
    this.hands = hands;
  }

  public int countHands() {
    return this.hands.size();
  }

  public void play(ArrayList<Card> cards) {
    for (Card card : cards) {
      this.hands.remove(card);
    }
  }
}
