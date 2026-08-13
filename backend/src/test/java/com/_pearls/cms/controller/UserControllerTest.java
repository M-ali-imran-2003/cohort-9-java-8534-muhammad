package com._pearls.cms.controller;

import com._pearls.cms.dto.ChangePasswordRequest;
import com._pearls.cms.dto.ProfileResponse;
import com._pearls.cms.dto.SuccessResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.service.JwtService;
import com._pearls.cms.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private static final Long TEST_USER_ID = 1005L;

    @BeforeEach
    void setupSecurityContext() {
        User principal = new User();
        principal.setId(TEST_USER_ID);

        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, AuthorityUtils.NO_AUTHORITIES);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void changePasswordSuccess() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("12345678", "12344321");
        when(userService.changePassword(eq(TEST_USER_ID), any(ChangePasswordRequest.class)))
                .thenReturn(new SuccessResponse("Password Changed Successfully"));

        mockMvc.perform(post("/api/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void changePasswordInvalidBody() throws Exception {
        ChangePasswordRequest invalidRequest = new ChangePasswordRequest("", "");

        mockMvc.perform(post("/api/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}