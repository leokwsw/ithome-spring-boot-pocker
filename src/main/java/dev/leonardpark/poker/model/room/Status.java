package dev.leonardpark.poker.model.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Status {
  private boolean inRoom = false;
  private String roomId = "";
  private boolean ready = false;
}
