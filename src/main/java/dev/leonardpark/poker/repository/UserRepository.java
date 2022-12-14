package dev.leonardpark.poker.repository;

import dev.leonardpark.poker.entity.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserModel, String> {
  Optional<UserModel> findById(int id);

  Optional<UserModel> findByUsername(String username);

  Optional<UserModel> findByEmail(String email);
}
