package dev.leonardpark.poker.model.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuitRoomMessage {
  private String username;
  private String roomId;
}
