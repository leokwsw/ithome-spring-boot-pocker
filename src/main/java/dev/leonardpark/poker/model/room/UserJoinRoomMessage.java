package dev.leonardpark.poker.model.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJoinRoomMessage {
  private String action;
  private String roomId;
}
