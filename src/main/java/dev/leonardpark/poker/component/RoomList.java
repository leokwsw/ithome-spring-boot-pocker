package dev.leonardpark.poker.component;

import dev.leonardpark.poker.model.room.Room;
import dev.leonardpark.poker.utils.RandomString;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class RoomList {
  private final Map<String, Room> roomList = new HashMap<>();

  public boolean add(String roomId, Room room) {
    if (containRoomId(roomId)) return false;
    roomList.put(roomId, room);
    return true;
  }

  public String create(String username) {
    String roomId;
    do roomId = RandomString.get(8);
    while (containRoomId(roomId));
    Room newRoom = new Room(roomId, username);
    add(roomId, newRoom);
    return roomId;
  }

  public boolean containRoomId(String roomId) {
    return roomList.containsKey(roomId);
  }

  public Room getRoomById(String roomId) {
    if (containRoomId(roomId)) return roomList.get(roomId);
    return null;
  }

  public HashSet<Room> getRooms() {
    return new HashSet<>(roomList.values());
  }

  public void destroy(String roomId) {
    this.roomList.remove(roomId);
  }
}
