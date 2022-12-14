package dev.leonardpark.poker.controller;

import dev.leonardpark.poker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {
  @Autowired
  private UserService userService;

  @GetMapping("/game/{roomId}")
  public String viewGamePage(@PathVariable("roomId") String roomId, Model model) {
    model.addAttribute("username", userService.getUsername());

    return "game";
  }
}
