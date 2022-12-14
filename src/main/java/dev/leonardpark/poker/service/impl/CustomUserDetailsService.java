package dev.leonardpark.poker.service.impl;

import dev.leonardpark.poker.entity.UserModel;
import dev.leonardpark.poker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  @Autowired
  UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserModel user = userService.getByUserName(username);
    if (user == null) {
      throw new UsernameNotFoundException("該帳號不存在");
    }
    return new CustomUserDetails(user);
  }
}
