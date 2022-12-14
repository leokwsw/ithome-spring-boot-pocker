package dev.leonardpark.poker.utils;

import java.util.Locale;
import java.util.Random;

public class RandomString {
  public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String lower = upper.toLowerCase(Locale.ROOT);
  public static final String digits = "0123456789";
  public static final String symbols = upper + lower + digits;

  public static String get(int length) {
    StringBuilder result = new StringBuilder();
    char[] array = symbols.toCharArray();
    for (int i = 0; i < length; i++) {
      int randomInt = new Random().nextInt(array.length);
      result.append(array[randomInt]);
    }
    return result.toString();
  }
}
