package com._pearls.cms.controller;

import com._pearls.cms.dto.ChangePasswordRequest;
import com._pearls.cms.dto.ProfileResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal User user){
        ProfileResponse profile = userService.getProfile(user.getId());

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user, @Valid @RequestBody ChangePasswordRequest request){
        String response = userService.changePassword(user.getId(),request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
