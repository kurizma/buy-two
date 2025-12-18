package com.buyone.userservice.repository;

import com.buyone.userservice.model.User;
import com.buyone.userservice.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
