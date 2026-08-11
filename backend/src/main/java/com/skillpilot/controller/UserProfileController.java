package com.skillpilot.controller;

import com.skillpilot.dto.request.ProfileUpdateRequest;
import com.skillpilot.dto.response.UserProfileResponse;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserProfileResponse profile = userProfileService.getUserProfile(securityUser.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody ProfileUpdateRequest request) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserProfileResponse updated = userProfileService.updateUserProfile(securityUser.getId(), request);
        return ResponseEntity.ok(updated);
    }
}
