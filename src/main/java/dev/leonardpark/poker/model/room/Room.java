package dev.leonardpark.poker.model.room;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class Room {
  private String owner;
  private ArrayList<String> guests;
  private String roomId;

  public Room(String roomId, String owner) {
    this.owner = owner;
    this.guests = new ArrayList<>();
    this.roomId = roomId;
  }

  public boolean addGuest(String guestName) {
    if (this.guests.size() >= 3) return false;
    this.guests.add(guestName);
    return true;
  }

  public void removeGuest(String guestName) {
    this.guests.remove(guestName);
  }

  public int count() {
    return this.guests.size() + 1;
  }

  public ArrayList<String> getAllMembers() {
    ArrayList<String> all = new ArrayList<>();
    all.add(this.owner);
    all.addAll(this.guests);
    return all;
  }

  public Map<String, Object> getInfo() {
    Map<String, Object> info = new HashMap<>();
    info.put("roomId", this.roomId);
    info.put("owner", this.owner);
    info.put("guests", this.guests);
    info.put("number", this.count());
    return info;
  }
}
