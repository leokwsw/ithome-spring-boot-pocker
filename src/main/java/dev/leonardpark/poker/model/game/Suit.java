package dev.leonardpark.poker.model.game;

public enum Suit {
  SPADE(4),
  HEART(3),
  DIAMOND(2),
  CLUB(1);

  private final int level;

  Suit(int level) {
    this.level = level;
  }

  // 大於
  public boolean greaterThan(Suit other) {
    return this.level > other.level;
  }

  // 小於
  public boolean lessThan(Suit other) {
    return this.level < other.level;
  }

  // 相等
  public boolean equals(Suit other) {
    return this.level == other.level;
  }
}
