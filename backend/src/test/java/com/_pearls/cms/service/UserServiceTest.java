package com._pearls.cms.service;


import com._pearls.cms.dto.*;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.ResourceAlreadyExistsException;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;


    @Test
    void profileSuccess() {

        // Arrange
        Long id = 1005L;
        User user = new User(id,"Momin","momin@gmail.com","03214556577","12345678", LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        ProfileResponse response = userService.getProfile(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    void profileFail() {

        // Arrange
        Long id = 1005L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile(id));
    }

    @Test
    void changePasswordSuccess() {

        // Arrange
        Long id = 1005L;
        ChangePasswordRequest request = new ChangePasswordRequest("12345678","12344321");
        User user = new User(id,"Momin","momin@gmail.com","03214556577","12345678", LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(encoder.matches(request.getCurrentPassword(),user.getPassword())).thenReturn(true);
        when(encoder.encode(request.getNewPassword())).thenReturn("encodedPassword123");

        // Act
        String response = userService.changePassword(id, request);

        // Assert
        assertNotNull(response);
        assertEquals("Password Changed Successfully", response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changePasswordFailUserNotFound() {

        // Arrange
        Long id = 1005L;
        ChangePasswordRequest request = new ChangePasswordRequest("12345678","12344321");
        User user = new User(id,"Momin","momin@gmail.com","03214556577","12345678", LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> userService.changePassword(id,request));
    }

    @Test
    void changePasswordFailCurrentNewPasswordSame() {

        // Arrange
        Long id = 1005L;
        ChangePasswordRequest request = new ChangePasswordRequest("12345678","12345678");
        User user = new User(id,"Momin","momin@gmail.com","03214556577","12345678", LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // Act
        assertThrows(BadCredentialsException.class, () -> userService.changePassword(id,request));
    }

    @Test
    void changePasswordFailInvalidCurrentPassword() {

        // Arrange
        Long id = 1005L;
        ChangePasswordRequest request = new ChangePasswordRequest("12345678","amu030615");
        User user = new User(id,"Momin","momin@gmail.com","03214556577","12345678", LocalDateTime.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(encoder.matches(request.getCurrentPassword(),user.getPassword())).thenReturn(false);

        // Act
        assertThrows(BadCredentialsException.class, () -> userService.changePassword(id,request));
    }
}
