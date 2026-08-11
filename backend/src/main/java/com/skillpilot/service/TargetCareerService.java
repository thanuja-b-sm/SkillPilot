package com.skillpilot.service;

import com.skillpilot.dto.response.TargetCareerResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserTargetCareer;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.repository.UserTargetCareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TargetCareerService {

    private final UserTargetCareerRepository userTargetCareerRepository;
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    @Transactional
    public TargetCareerResponse setTargetCareer(String userId, String careerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", careerId));

        if (!Boolean.TRUE.equals(career.getIsActive())) {
            throw new BadRequestException("Selected career is inactive and cannot be chosen as a target career.");
        }

        UserTargetCareer targetCareer = userTargetCareerRepository.findByUserId(userId)
                .orElseGet(() -> UserTargetCareer.builder()
                        .id(UUID.randomUUID().toString())
                        .user(user)
                        .build());

        targetCareer.setCareer(career);
        UserTargetCareer saved = userTargetCareerRepository.save(targetCareer);

        return TargetCareerResponse.builder()
                .careerId(saved.getCareer().getId())
                .careerName(saved.getCareer().getTitle())
                .selectedAt(saved.getSelectedAt())
                .active(saved.getCareer().getIsActive())
                .build();
    }

    @Transactional(readOnly = true)
    public TargetCareerResponse getTargetCareer(String userId) {
        UserTargetCareer targetCareer = userTargetCareerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TargetCareer", "userId", userId));

        return TargetCareerResponse.builder()
                .careerId(targetCareer.getCareer().getId())
                .careerName(targetCareer.getCareer().getTitle())
                .selectedAt(targetCareer.getSelectedAt())
                .active(targetCareer.getCareer().getIsActive())
                .build();
    }
}
