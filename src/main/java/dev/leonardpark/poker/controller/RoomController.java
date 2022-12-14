package dev.leonardpark.poker.controller;

import dev.leonardpark.poker.component.RoomList;
import dev.leonardpark.poker.component.UserStatus;
import dev.leonardpark.poker.model.room.UserJoinRoomMessage;
import dev.leonardpark.poker.model.room.UserQuitRoomMessage;
import dev.leonardpark.poker.service.RoomService;
import dev.leonardpark.poker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RoomController {
  @Autowired
  private UserService userService;

  @Autowired
  private UserStatus userStatus;

  @Autowired
  private RoomService roomService;

  @Autowired
  private RoomList roomList;

  @GetMapping("/rooms")
  public String viewAllRoomsPage(Model model) {
    if (userService.isLogin()) {
      String username = userService.getUsername();
      if (this.userStatus.containsUser(username) && this.userStatus.isUserInRoom(username))
        return "redirect:/room/" + this.userStatus.getUserRoomId(username);
      this.userStatus.initialize(username);
    }
    model.addAttribute("disableJoinRoomButton", !userService.isLogin());
    return "rooms";
  }

  @GetMapping("/room/{roomId}")
  public String viewRoomPage(@PathVariable("roomId") String roomId, Model model) {
    // 如果房號不存在
    if (!this.roomList.containRoomId(roomId)) {
      return "redirect:/rooms";
    }

    // 使用者必須先登入
    if (userService.isLogin()) {
      String username = userService.getUsername();
      model.addAttribute("username", username);
      // 如果使用者有狀態，而且儲存的房號跟網址相同
      if (this.userStatus.containsUser(username) && this.userStatus.getUserRoomId(username).equals(roomId))
        return "room";
      // 否則，讓使用者加入該房間
      if (roomService.joinInRoom(username, roomId)) return "room";
    }

    // 沒有登入的話，就不給進
    return "redirect:/rooms";
  }

  @PostMapping("/api/room/join")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> joinRoomProcess(UserJoinRoomMessage userJoinRoomMessage) {
    Map<String, Object> response = new HashMap<>();
    HttpStatus httpStatus;
    String message;
    if (!userService.isLogin()) {
      httpStatus = HttpStatus.FORBIDDEN;
      message = "未登入";
      response.put("message", message);
      return ResponseEntity.status(httpStatus).body(response);
    }

    String username = userService.getUsername();
    String action = userJoinRoomMessage.getAction();
    String roomId = userJoinRoomMessage.getRoomId();
    if (action.equals("create") && roomService.createRoom(username)) {
      httpStatus = HttpStatus.OK;
      message = "建立成功";
    } else if (action.equals("join") && roomService.joinInRoom(username, roomId)) {
      httpStatus = HttpStatus.OK;
      message = "加入成功";
    } else {
      httpStatus = HttpStatus.BAD_REQUEST;
      message = "Error";
    }

    if (httpStatus == HttpStatus.OK) {
      // 發送最新的房間列表資訊
      this.roomService.broadcastRoomList();
      roomId = this.userStatus.getUserRoomId(username);
    } else {
      roomId = "";
    }
    response.put("message", message);
    response.put("roomId", roomId);
    return ResponseEntity.status(httpStatus).body(response);
  }

  @PostMapping("/room/quit")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> quitRoomProcess(UserQuitRoomMessage userQuitRoomMessage) {
    Map<String, Object> response = new HashMap<>();
    HttpStatus httpStatus;
    String message;

    if (!userService.isLogin()) {
      httpStatus = HttpStatus.FORBIDDEN;
      message = "未登入";
      response.put("message", message);
      return ResponseEntity.status(httpStatus).body(response);
    }

    String username = userQuitRoomMessage.getUsername();
    String roomId = userQuitRoomMessage.getRoomId();
    String me = userService.getUsername();

    // 避免有人利用 API 在非房主的情況下把其他人踢掉
    if (!username.equals(me) && !roomList.getRoomById(roomId).getOwner().equals(me)) {
      httpStatus = HttpStatus.FORBIDDEN;
      message = "權限不足";
      response.put("message", message);
      return ResponseEntity.status(httpStatus).body(response);
    }

    // 將使用者移除
    if (roomService.quitRoom(username, roomId)) {
      httpStatus = HttpStatus.OK;
      message = "成功";
      roomService.sendRoomInfo(roomId);
    } else {
      httpStatus = HttpStatus.BAD_REQUEST;
      message = "Error";
    }
    response.put("message", message);
    return ResponseEntity.status(httpStatus).body(response);
  }
}
