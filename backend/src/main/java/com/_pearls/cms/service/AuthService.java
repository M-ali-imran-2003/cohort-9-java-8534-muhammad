package com._pearls.cms.service;

import com._pearls.cms.dto.LoginRequest;
import com._pearls.cms.dto.LoginResponse;
import com._pearls.cms.dto.RegisterRequest;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.ResourceAlreadyExistsException;
import com._pearls.cms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    public String register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
                log.warn("User with email already exists: {}", registerRequest.getEmail());
                throw new ResourceAlreadyExistsException("User with the email "+registerRequest.getEmail()+" already exist.");
        }
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            log.warn("User with phone already exists: {}", registerRequest.getPhone());
            throw new ResourceAlreadyExistsException("User with the phone "+registerRequest.getPhone()+" already exist.");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
        return "Registration Successful";
    }

    public LoginResponse login(LoginRequest loginRequest) {

        User dbUser = userRepository.findByEmailOrPhone(
                loginRequest.getIdentifier()
        );

        if (dbUser != null) {

            if (encoder.matches(loginRequest.getPassword(), dbUser.getPassword())) {
                log.info("User Logged In successfully");
                return new LoginResponse(jwtService.generateToken(dbUser.getId()));
            }
            else{
                log.warn("Password does not match for: {}",loginRequest.getIdentifier());
                throw new BadCredentialsException("Provided credentials are incorrect");
            }

        }
        else{
            log.warn("User Not Found with the provided Email or Phone: {}",loginRequest.getIdentifier());
            throw new BadCredentialsException("Provided credentials are incorrect");
        }

    }
}
