package com.skillpilot.controller;

import com.skillpilot.dto.request.TargetCareerRequest;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.TargetCareerResponse;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.SkillGapService;
import com.skillpilot.service.TargetCareerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TargetCareerController {

    private final TargetCareerService targetCareerService;
    private final SkillGapService skillGapService;

    @PutMapping("/api/user/target-career")
    public ResponseEntity<TargetCareerResponse> setTargetCareer(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody TargetCareerRequest request) {
        TargetCareerResponse response = targetCareerService.setTargetCareer(securityUser.getId(), request.getCareerId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/user/target-career")
    public ResponseEntity<TargetCareerResponse> getTargetCareer(
            @AuthenticationPrincipal SecurityUser securityUser) {
        TargetCareerResponse response = targetCareerService.getTargetCareer(securityUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/user/target-career/skill-gap")
    public ResponseEntity<SkillGapAnalysisResponse> getTargetCareerSkillGap(
            @AuthenticationPrincipal SecurityUser securityUser) {
        SkillGapAnalysisResponse response = skillGapService.getSkillGapForTargetCareer(securityUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/careers/{careerId}/skill-gap")
    public ResponseEntity<SkillGapAnalysisResponse> getCareerSkillGap(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable String careerId) {
        SkillGapAnalysisResponse response = skillGapService.getSkillGapForCareer(securityUser.getId(), careerId);
        return ResponseEntity.ok(response);
    }
}
