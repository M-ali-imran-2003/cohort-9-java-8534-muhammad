package com._pearls.cms.controller;

import com._pearls.cms.dto.LoginRequest;
import com._pearls.cms.dto.LoginResponse;
import com._pearls.cms.dto.RegisterRequest;
import com._pearls.cms.exception.ResourceAlreadyExistsException;
import com._pearls.cms.service.AuthService;
import com._pearls.cms.service.JwtService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("Momin", "momin@gmail.com", "03214125522", "12345678");
        when(authService.register(any(RegisterRequest.class))).thenReturn("Registration Successful");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration Successful"));
    }

    @Test
    void registerConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("Momin", "momin@gmail.com", "03214125522", "12345678");
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new ResourceAlreadyExistsException("User with the email momin@gmail.com already exist."));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerInvalidBody() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("", "not-an-email", "", "short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("momin@gmail.com", "12345678");
        when(authService.login(any(LoginRequest.class))).thenReturn(new LoginResponse("jwt-token-123"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("jwt-token-123")));
    }

    @Test
    void loginUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("momin@gmail.com", "wrongpass");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Provided credentials are incorrect"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginInvalid() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}