package dev.leonardpark.poker.controller;

import dev.leonardpark.poker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @Autowired
  private UserService userService;

  @GetMapping({"/", "/home"})
  public String viewHomePage(Model model) {
    String name = userService.getUsername();
    model.addAttribute("name", name);
    System.out.println("mooooooooooooooooooooooo home mo");
    return "home";
  }
}
