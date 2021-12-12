package com.example.app;

import com.example.app.db.UserRepository;

import java.util.Optional;

public class Authorizer {

    private final UserRepository userRepository;

    public Authorizer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> auth(String username, String password) {
        return Optional.empty();
    }
}
