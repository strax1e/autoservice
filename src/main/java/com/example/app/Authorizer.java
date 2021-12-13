package com.example.app;

import com.example.app.db.UserRepository;
import com.example.app.entity.User;

import java.util.Optional;

public class Authorizer {

    private final UserRepository userRepository;

    public Authorizer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> auth(String username, String password) {
        final var user = userRepository.getUserByUsername(username);
        return user.isPresent() && user.get().password().equals(password)
                ? user
                : Optional.empty();
    }
}
