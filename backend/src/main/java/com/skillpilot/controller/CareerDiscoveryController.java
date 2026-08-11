package com.skillpilot.controller;

import com.skillpilot.dto.response.CareerMatchResponse;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.CareerDiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CareerDiscoveryController {

    private final CareerDiscoveryService careerDiscoveryService;

    @GetMapping("/careers/matches")
    public ResponseEntity<List<CareerMatchResponse>> getCareerMatches(
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(securityUser.getId());
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/user/career-results")
    public ResponseEntity<List<CareerMatchResponse>> getUserCareerResults(
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        List<CareerMatchResponse> matches = careerDiscoveryService.getUserCareerMatches(securityUser.getId());
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/careers/matches/recalculate")
    public ResponseEntity<List<CareerMatchResponse>> recalculateMatches(
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(securityUser.getId());
        return ResponseEntity.ok(matches);
    }
}
