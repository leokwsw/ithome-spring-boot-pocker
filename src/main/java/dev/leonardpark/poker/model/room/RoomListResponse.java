package dev.leonardpark.poker.model.room;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class RoomListResponse {
  @Getter
  @Setter
  private Set<Room> rooms;
}
