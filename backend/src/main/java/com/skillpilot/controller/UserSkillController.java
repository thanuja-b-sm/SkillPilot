package com.skillpilot.controller;

import com.skillpilot.dto.request.UserSkillUpdateRequest;
import com.skillpilot.dto.response.UserSkillResponse;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.UserSkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/skills")
public class UserSkillController {

    private final UserSkillService userSkillService;

    public UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService;
    }

    @GetMapping
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(@AuthenticationPrincipal SecurityUser securityUser) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<UserSkillResponse> skills = userSkillService.getUserSkills(securityUser.getId());
        return ResponseEntity.ok(skills);
    }

    @PutMapping
    public ResponseEntity<UserSkillResponse> updateUserSkill(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody UserSkillUpdateRequest request) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserSkillResponse response = userSkillService.updateUserSkill(securityUser.getId(), request);
        return ResponseEntity.ok(response);
    }
}
