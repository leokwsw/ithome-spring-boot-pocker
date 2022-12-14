package dev.leonardpark.poker.service;

public interface RoomService {
  boolean createRoom(String username);

  boolean joinInRoom(String username, String roomId);

  void broadcastRoomList();

  void sendMessageToRoom(String roomId, String destination, Object message);

  void sendRoomInfo(String roomId);

  boolean quitRoom(String user, String roomId);
}
