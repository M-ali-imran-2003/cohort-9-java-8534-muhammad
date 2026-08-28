package com._pearls.cms.controller;

import com._pearls.cms.dto.LoginRequest;
import com._pearls.cms.dto.LoginResponse;
import com._pearls.cms.dto.RegisterRequest;
import com._pearls.cms.dto.SuccessResponse;
import com._pearls.cms.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
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
    public ResponseEntity<SuccessResponse> register(@Valid @RequestBody RegisterRequest registerRequest)
    {
        SuccessResponse response =  authService.register(registerRequest);

            return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response)
    {
        authService.login(loginRequest, response);
            return new ResponseEntity<>(new SuccessResponse("Login Successful"),HttpStatus.OK);

    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return new ResponseEntity<>(new SuccessResponse("Logged out successfully"), HttpStatus.OK);
    }
}
