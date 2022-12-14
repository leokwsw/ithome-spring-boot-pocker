package dev.leonardpark.poker.service;

import dev.leonardpark.poker.entity.UserModel;

public interface UserService {
  UserModel getById(int id);

  UserModel getByUserName(String userName);

  UserModel getByEmail(String email);

  int register(UserModel userModel);

  boolean isLogin();

  String getUsername();
}
