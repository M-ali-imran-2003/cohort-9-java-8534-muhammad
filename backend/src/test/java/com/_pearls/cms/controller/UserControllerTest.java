package com._pearls.cms.controller;

import com._pearls.cms.dto.ChangePasswordRequest;
import com._pearls.cms.dto.ProfileResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.service.JwtService;
import com._pearls.cms.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private UserService userService;

    private UsernamePasswordAuthenticationToken authFor(Long userId) {
        User principal = new User();
        principal.setId(userId);
        return new UsernamePasswordAuthenticationToken(
                principal, null, AuthorityUtils.NO_AUTHORITIES);
    }

    @Test
    void getProfileSuccess() throws Exception {
        ProfileResponse response = new ProfileResponse(
                1005L, "Momin", "momin@gmail.com", "03214556577", LocalDateTime.now());
        when(userService.getProfile(1005L)).thenReturn(response);

        mockMvc.perform(get("/api/user/get-profile")
                        .with(authentication(authFor(1005L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1005))
                .andExpect(jsonPath("$.email").value("momin@gmail.com"));
    }

    @Test
    void getProfileNotFound() throws Exception {
        when(userService.getProfile(1005L))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/user/get-profile")
                        .with(authentication(authFor(1005L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void changePasswordSuccess() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("12345678", "12344321");
        when(userService.changePassword(eq(1005L), any(ChangePasswordRequest.class)))
                .thenReturn("Password Changed Successfully");

        mockMvc.perform(put("/api/user/change-password")
                        .with(authentication(authFor(1005L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordInvalidBody() throws Exception {
        ChangePasswordRequest invalidRequest = new ChangePasswordRequest("", "");

        mockMvc.perform(put("/api/user/change-password")
                        .with(authentication(authFor(1005L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePasswordWrongCurrentPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongpass", "newpass123");
        when(userService.changePassword(eq(1005L), any(ChangePasswordRequest.class)))
                .thenThrow(new BadCredentialsException("Current Password not valid"));

        mockMvc.perform(put("/api/user/change-password")
                        .with(authentication(authFor(1005L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePasswordUserNotFound() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("12345678", "newpass123");
        when(userService.changePassword(any(Long.class), any(ChangePasswordRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(put("/api/user/change-password")
                        .with(authentication(authFor(1005L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}