package com._pearls.cms.service;

import com._pearls.cms.dto.LoginRequest;
import com._pearls.cms.dto.RegisterRequest;
import com._pearls.cms.dto.SuccessResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.ResourceAlreadyExistsException;
import com._pearls.cms.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest("Momin", "momin@gmail.com", "03214125522", "12345678");
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(encoder.encode(request.getPassword())).thenReturn("encodedPassword123");

        SuccessResponse created = authService.register(request);

        assertNotNull(created);
        assertEquals("Registration Successful", created.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerFailEmail() {
        RegisterRequest request = new RegisterRequest("Momin", "momin@gmail.com", "03214125522", "12345678");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerFailPhone() {
        RegisterRequest request = new RegisterRequest("Momin", "momin@gmail.com", "03214125522", "12345678");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginSuccess() {
        LoginRequest request = new LoginRequest("03214556577", "12345678");
        User user = new User(1001L, "Momin", "momin@gmail.com", "03214556577", "12345678", LocalDateTime.now());
        when(userRepository.findByEmailOrPhone(request.getIdentifier())).thenReturn(user);
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getId())).thenReturn("jwt-token-123");

        assertDoesNotThrow(() -> authService.login(request, response));

        verify(response, times(1)).addCookie(any());
    }

    @Test
    void loginFailUserNotFound() {
        LoginRequest request = new LoginRequest("03214556577", "12345678");
        when(userRepository.findByEmailOrPhone(request.getIdentifier())).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> authService.login(request, response));
        verify(response, never()).addCookie(any());
    }

    @Test
    void loginFailPasswordIncorrect() {
        LoginRequest request = new LoginRequest("03214556577", "12345678");
        User user = new User(1001L, "Momin", "momin@gmail.com", "03214556577", "56784122", LocalDateTime.now());
        when(userRepository.findByEmailOrPhone(request.getIdentifier())).thenReturn(user);
        when(encoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request, response));
        verify(response, never()).addCookie(any());
    }
}