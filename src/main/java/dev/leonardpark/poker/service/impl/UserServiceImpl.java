package dev.leonardpark.poker.service.impl;

import dev.leonardpark.poker.entity.UserModel;
import dev.leonardpark.poker.repository.UserRepository;
import dev.leonardpark.poker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

@Service
@Validated
public class UserServiceImpl implements UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Validator validator;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserModel getById(int id) {
    Optional<UserModel> user = userRepository.findById(id);
    return user.orElse(null);
  }

  @Override
  public UserModel getByUserName(String userName) {
    Optional<UserModel> user = userRepository.findByUsername(userName);
    return user.orElse(null);
  }

  @Override
  public UserModel getByEmail(String email) {
    Optional<UserModel> user = userRepository.findByEmail(email);
    return user.orElse(null);
  }

  @Override
  public int register(UserModel userModel) {
    Set<ConstraintViolation<UserModel>> violations = validator.validate(userModel);
    if (!violations.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (ConstraintViolation<UserModel> constraintViolation : violations) {
        sb.append(constraintViolation.getMessage());
      }
      throw new ConstraintViolationException(sb.toString(), violations);
    }
    userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
    UserModel user = userRepository.save(userModel);
    return user.getId();
  }

  @Override
  public boolean isLogin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return !(authentication == null || authentication instanceof AnonymousAuthenticationToken);
  }

  @Override
  public String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
