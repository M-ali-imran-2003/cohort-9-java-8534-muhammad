package com._pearls.cms.service;

import com._pearls.cms.dto.LoginRequest;
import com._pearls.cms.dto.RegisterRequest;
import com._pearls.cms.entity.User;
import com._pearls.cms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(RegisterRequest registerRequest) {
        User dbUser = userRepository.findByEmailOrPhone(
                registerRequest.getEmail(),
                registerRequest.getPhone()
        );

        if (dbUser != null) {

            if (dbUser.getEmail() != null && dbUser.getEmail().equalsIgnoreCase(registerRequest.getEmail())) {
                log.error("Email already exists: {}",registerRequest.getEmail());
                throw new RuntimeException("Email already exists");
            }

            if (dbUser.getPhone() != null && dbUser.getPhone().equals(registerRequest.getPhone())) {
                log.error("Phone already exists: {}",registerRequest.getPhone());
                throw new RuntimeException("Phone number already exists");
            }
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User registered successfully: {}", user.toString());
    }

    public User login(LoginRequest loginRequest) {
        User dbUser = userRepository.findByEmailOrPhone(
                loginRequest.getIdentifier(),
                loginRequest.getIdentifier()
        );

        if (dbUser != null) {

            if(Objects.equals(loginRequest.getPassword(), dbUser.getPassword())){
                log.info("User Logged In successfully");
                return dbUser;
            }

        }

        log.warn("User Not Found with the provided credentials");
        throw new RuntimeException("Invalid Credentials");

    }


    }
