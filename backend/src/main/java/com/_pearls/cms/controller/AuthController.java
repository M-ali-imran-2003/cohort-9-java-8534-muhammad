package com._pearls.cms.controller;

import com._pearls.cms.dto.LoginRequest;
import com._pearls.cms.dto.RegisterRequest;
import com._pearls.cms.entity.User;
import com._pearls.cms.service.AuthService;
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
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest)
    {
        try {
            // Happy path: try to register the user
            authService.registerUser(registerRequest);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User registered successfully!");

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // This sends status code 400
                    .body(ex.getMessage());         // This sends the exact error message text
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest)
    {
        try {
            User user = authService.login(loginRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(user);
        }
        catch (RuntimeException ex) {
            // Error path: catch the validation errors (like "Email already exists")
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // This sends status code 400
                    .body(ex.getMessage());         // This sends the exact error message text
        }
    }
}
