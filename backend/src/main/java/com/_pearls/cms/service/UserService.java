package com._pearls.cms.service;

import com._pearls.cms.dto.ChangePasswordRequest;
import com._pearls.cms.dto.ProfileResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.exception.ResourceNotFoundException;
import com._pearls.cms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
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

    public String changePassword(Long id, ChangePasswordRequest request){
        Optional<User> dbUser = userRepository.findById(
                id
        );
        if(request.getCurrentPassword().equals(request.getNewPassword())){
            log.warn("New Password and Current password cannot be same");
            throw new BadCredentialsException("New Password and Current password cannot be same");
        }
        if (dbUser.isPresent())
        {
            User user = dbUser.get();
            if(encoder.matches(request.getCurrentPassword(),user.getPassword()))
            {
                user.setPassword(encoder.encode(request.getNewPassword()));
                userRepository.save(user);
                log.warn("Password Changed Successfully");
                return "Password Changed Successfully";
            }
            else{
                throw new BadCredentialsException("Current Password not valid");
            }
        }
        else {
            log.warn("User not found");
            throw new ResourceNotFoundException("User not found");
        }
    }
}
