package dev.leonardpark.poker.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
  private final ArrayList<Card> deck = new ArrayList<>();

  public Deck() {
    for (Suit suit : Suit.values()) {
      for (Number number : Number.values()) {
        this.deck.add(new Card(suit, number));
      }
    }
  }

  // 洗牌
  public void shuffle() {
    Collections.shuffle(this.deck);
  }

  // 發牌(分成 4 份)
  public ArrayList<ArrayList<Card>> deal() {
    ArrayList<ArrayList<Card>> result = new ArrayList<>();
    int deckSize = this.deck.size();
    int numberPerPerson = deckSize / 4;
    int start = 0;
    for (int i = 0; i < 4; i++) {
      List<Card> tmp = this.deck.subList(start, start + numberPerPerson);
      result.add(new ArrayList<>(tmp));
      start += numberPerPerson;
    }
    while (start < deckSize) result.get(deckSize - start - 1).add(this.deck.get(start++));
    return result;
  }
}
