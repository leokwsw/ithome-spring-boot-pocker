package dev.leonardpark.poker.service.impl;

import dev.leonardpark.poker.component.RoomList;
import dev.leonardpark.poker.component.UserStatus;
import dev.leonardpark.poker.model.room.Room;
import dev.leonardpark.poker.model.room.RoomListResponse;
import dev.leonardpark.poker.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoomServiceImpl implements RoomService {
  @Autowired
  private RoomList roomList;

  @Autowired
  private UserStatus userStatus;

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  @Override
  public boolean createRoom(String username) {
    // 檢查使用者已經有狀態，並且目前不在任何房間內
    if (this.userStatus.containsUser(username) && this.userStatus.isUserInRoom(username)) return false;
    // 建立房間
    String roomId = roomList.create(username);
    // 修改使用者目前的狀態
    this.userStatus.setUserInRoom(username, true);
    this.userStatus.setUserRoomId(username, roomId);
    return true;
  }

  @Override
  public boolean joinInRoom(String username, String roomId) {
    // 檢查使用者已經有狀態，並且目前不在任何房間內
    if (this.userStatus.containsUser(username) && this.userStatus.isUserInRoom(username)) return false;
    // 檢查要加入的房間人數小於 4
    if (roomList.getRoomById(roomId) == null) return false;
    if (roomList.getRoomById(roomId).count() == 4) return false;

    // 將使用者加入該房間
    Room room = roomList.getRoomById(roomId);
    if (room.addGuest(username)) {
      if (!this.userStatus.containsUser(username)) this.userStatus.initialize(username);
      this.userStatus.setUserInRoom(username, true);
      this.userStatus.setUserRoomId(username, roomId);
      return true;
    }
    return false;
  }

  @Override
  public void broadcastRoomList() {
    RoomListResponse roomListResponse = new RoomListResponse();
    roomListResponse.setRooms(roomList.getRooms());
    this.simpMessagingTemplate.convertAndSend("/topic/room-list", roomListResponse);
  }

  @Override
  public void sendMessageToRoom(String roomId, String destination, Object message) {
    // 取得指定房間
    Room room = roomList.getRoomById(roomId);

    // 取得房間內的所有成員
    ArrayList<String> roomMembers = room.getAllMembers();

    // 發送資訊給所有房間內的成員
    for (String member : roomMembers) {
      simpMessagingTemplate.convertAndSendToUser(member, destination, message);
    }
  }

  @Override
  public void sendRoomInfo(String roomId) {
    Room room = roomList.getRoomById(roomId);
    ArrayList<String> roomMembers = room.getAllMembers();

    // 把使用者是否準備的狀態取出
    Map<String, Boolean> userReadyStatus = new HashMap<>();
    for (String member : roomMembers) {
      userReadyStatus.put(member, this.userStatus.isUserReady(member));
    }
    Map<String, Object> response = new HashMap<>();
    response.put("userStatus", userReadyStatus);

    // 取得房間資訊
    response.put("roomInfo", room.getInfo());

    // 發送給該房間的所有人
    sendMessageToRoom(roomId, "/queue/room-info", response);
  }

  @Override
  public boolean quitRoom(String user, String roomId) {
    // 根本沒有這個 user
    if (!this.userStatus.containsUser(user)) return false;

    // user 根本不在房間
    if (!this.userStatus.isUserInRoom(user)) return false;

    // 這個房號不存在
    if (!this.roomList.containRoomId(roomId)) return false;

    Room room = roomList.getRoomById(roomId);

    // 如果退出的是房主，要另外處理
    if (room.getOwner().equals(user)) {
      // 如果房間人數等於 1，表示整個房間只有房主，那就需要把這個房間銷毀
      // 否則，從其他成員選擇一人擔任新房主
      if (room.count() == 1)
        this.roomList.destroy(roomId);
      else {
        String newOwner = room.getAllMembers().get(1);
        room.setOwner(newOwner);
        room.removeGuest(newOwner);
        this.userStatus.setUserReady(newOwner, false);
      }
    } else room.removeGuest(user);

    this.userStatus.setUserInRoom(user, false);
    this.userStatus.setUserRoomId(user, "");

    Map<String, Object> response = new HashMap<>();
    response.put("roomInfo", room.getInfo());

    simpMessagingTemplate.convertAndSendToUser(user, "/queue/room-info", response);
    return true;
  }
}
