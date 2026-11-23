package com.feedback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedback.entity.User;
import com.feedback.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public String register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username already exists";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }

        user.setRole("USER");
        userRepository.save(user);
        return "Registration successful";
    }


    public User login(String username, String password) {

        User user=userRepository.findByUsername(username).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}