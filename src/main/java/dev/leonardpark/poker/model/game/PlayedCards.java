package dev.leonardpark.poker.model.game;

import java.util.ArrayList;

public class PlayedCards {
  private final ArrayList<Card> cards;

  public PlayedCards(ArrayList<Card> playedCards) {
    this.cards = playedCards;
  }

  public ArrayList<Card> get() {
    return this.cards;
  }

  public boolean isValid() {
    return Type.getType(this.get()) != null;
  }

  public Card getHighestLevelCard() {
    this.cards.sort(Card.getComparatorWithLevel());
    return this.cards.get(this.cards.size() - 1);
  }

  public static int compare(PlayedCards a, PlayedCards b) {
    if (a == null) return -1;
    if (b == null) return 1;
    Type aType = Type.getType(a.get());
    Type bType = Type.getType(b.get());
    if (bType != null && bType.levelGreaterThan(aType))
      return -1;
    else if (bType != null && bType.levelEquals(aType)) {
      if (bType == aType) {
        if (aType == Type.STRAIGHT || aType == Type.STRAIGHT_FLUSH) {
          boolean aIsAceToFive = a.get().get(3).getNumber().equals(Number.ACE);
          boolean bIsAceToFive = b.get().get(3).getNumber().equals(Number.ACE);
          if (aIsAceToFive && bIsAceToFive)
            return a.getHighestLevelCard().greaterThan(b.getHighestLevelCard()) ? 1 : -1;
          else {
            if (aIsAceToFive) return -1;
            else if (bIsAceToFive) return 1;
            else return a.getHighestLevelCard().greaterThan(b.getHighestLevelCard()) ? 1 : -1;
          }
        } else if (aType == Type.FULL_HOUSE || aType == Type.FOUR_OF_A_KIND)
          return a.getHighestLevelCard().greaterThan(b.getHighestLevelCard()) ? 1 : -1;
        else
          return a.getHighestLevelCard().greaterThan(b.getHighestLevelCard()) ? 1 : -1;
      } else return 0;
    }
    return 0;
  }

  public boolean canBePlayedOn(PlayedCards previous) {
    return PlayedCards.compare(this, previous) == 1;
  }
}
