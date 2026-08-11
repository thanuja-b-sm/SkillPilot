package com.skillpilot.controller;

import com.skillpilot.dto.request.SystemConfigUpdateRequest;
import com.skillpilot.dto.response.SystemConfigResponse;
import com.skillpilot.service.SystemConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping("/config")
    public ResponseEntity<SystemConfigResponse> getConfig() {
        return ResponseEntity.ok(systemConfigService.getCurrentConfig());
    }

    @PutMapping("/config")
    public ResponseEntity<SystemConfigResponse> updateConfig(@Valid @RequestBody SystemConfigUpdateRequest request) {
        return ResponseEntity.ok(systemConfigService.updateConfig(request));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(systemConfigService.getDashboardStats());
    }
}
