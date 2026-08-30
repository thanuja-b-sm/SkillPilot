package com.skillpilot.controller;

import com.skillpilot.dto.response.SavedCareerResponse;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.SavedCareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/saved-careers")
public class SavedCareerController {

    private final SavedCareerService savedCareerService;

    @GetMapping
    public ResponseEntity<List<SavedCareerResponse>> getSavedCareers(
            @AuthenticationPrincipal SecurityUser securityUser) {
        List<SavedCareerResponse> savedCareers = savedCareerService.getSavedCareersForUser(securityUser.getId());
        return ResponseEntity.ok(savedCareers);
    }

    @PostMapping("/{careerId}")
    public ResponseEntity<SavedCareerResponse> saveCareer(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable String careerId,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        SavedCareerResponse response = savedCareerService.saveCareer(securityUser.getId(), careerId, notes);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{careerId}")
    public ResponseEntity<Map<String, Object>> unsaveCareer(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable String careerId) {
        savedCareerService.unsaveCareer(securityUser.getId(), careerId);
        return ResponseEntity.ok(Map.of("message", "Career removed from saved list", "careerId", careerId, "saved", false));
    }

    @GetMapping("/{careerId}/status")
    public ResponseEntity<Map<String, Boolean>> isCareerSaved(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable String careerId) {
        boolean saved = savedCareerService.isCareerSaved(securityUser.getId(), careerId);
        return ResponseEntity.ok(Map.of("saved", saved));
    }
}
