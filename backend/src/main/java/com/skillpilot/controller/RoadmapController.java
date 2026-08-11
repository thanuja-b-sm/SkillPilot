package com.skillpilot.controller;

import com.skillpilot.dto.request.RoadmapGenerateRequest;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.RoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/api/user/roadmaps/generate")
    public ResponseEntity<CareerRoadmapResponse> generateRoadmap(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody(required = false) RoadmapGenerateRequest request) {
        int duration = (request != null && request.getDurationMonths() != null) ? request.getDurationMonths() : 6;
        CareerRoadmapResponse response = roadmapService.generateAndPersistRoadmap(securityUser.getId(), duration);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/user/roadmaps")
    public ResponseEntity<CareerRoadmapResponse> getUserRoadmap(
            @AuthenticationPrincipal SecurityUser securityUser) {
        CareerRoadmapResponse response = roadmapService.getRoadmapForUser(securityUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/user/roadmaps/{roadmapId}")
    public ResponseEntity<CareerRoadmapResponse> getRoadmapById(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable String roadmapId) {
        CareerRoadmapResponse response = roadmapService.getRoadmapById(securityUser.getId(), roadmapId);
        return ResponseEntity.ok(response);
    }
}
