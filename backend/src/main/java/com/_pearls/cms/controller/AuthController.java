package com._pearls.cms.controller;

import com._pearls.cms.dto.LoginRequest;
import com._pearls.cms.dto.RegisterRequest;
import com._pearls.cms.entity.User;
import com._pearls.cms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest)
    {
            authService.registerUser(registerRequest);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
    {

        User user = authService.login(loginRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(user);

    }
}
