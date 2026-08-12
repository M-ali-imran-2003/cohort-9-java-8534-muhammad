package com._pearls.cms.service;

import com._pearls.cms.dto.ChangePasswordRequest;
import com._pearls.cms.dto.ProfileResponse;
import com._pearls.cms.dto.SuccessResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.InvalidRequestException;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public ProfileResponse getProfile(Long id){
        Optional<User> dbUser = userRepository.findById(
                id
        );
        if (dbUser.isPresent())
        {
            User user = dbUser.get();
            log.info("User Found");
            return new ProfileResponse(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getCreatedAt());
        }
        else {
            log.warn("User with the id not found");
            throw new ResourceNotFoundException("User not found");
        }
    }

    public SuccessResponse changePassword(Long id, ChangePasswordRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found");
                    return new ResourceNotFoundException("User not found");
                });

        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Current password does not match for user");
            throw new BadCredentialsException("Current Password not valid");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            log.warn("New password same as current password for user");
            throw new InvalidRequestException("New Password and Current Password cannot be the same");
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user");
        return new SuccessResponse("Password Changed Successfully");
    }
}
