package com.skillpilot.controller;

import com.skillpilot.dto.response.CareerResponse;
import com.skillpilot.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/careers")
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;

    @GetMapping
    public ResponseEntity<List<CareerResponse>> getActiveCareers() {
        return ResponseEntity.ok(careerService.getActiveCareers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareerResponse> getCareerById(@PathVariable String id) {
        return ResponseEntity.ok(careerService.getCareerById(id));
    }
}
