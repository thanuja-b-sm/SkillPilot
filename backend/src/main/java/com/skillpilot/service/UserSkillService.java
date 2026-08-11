package com.skillpilot.service;

import com.skillpilot.dto.request.UserSkillUpdateRequest;
import com.skillpilot.dto.response.UserSkillResponse;
import com.skillpilot.entity.Skill;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserSkill;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.SkillRepository;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserProfileMapper userProfileMapper;
    private final CompletionCalculatorService completionCalculatorService;

    public UserSkillService(
            UserSkillRepository userSkillRepository,
            UserRepository userRepository,
            SkillRepository skillRepository,
            UserProfileMapper userProfileMapper,
            CompletionCalculatorService completionCalculatorService) {
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userProfileMapper = userProfileMapper;
        this.completionCalculatorService = completionCalculatorService;
    }

    @Transactional(readOnly = true)
    public List<UserSkillResponse> getUserSkills(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        return userSkillRepository.findByUserId(userId).stream()
                .map(userProfileMapper::toSkillResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSkillResponse updateUserSkill(String userId, UserSkillUpdateRequest request) {
        if (request == null || request.getSkillId() == null || request.getLevel() == null) {
            throw new BadRequestException("Skill ID and rating level are required");
        }

        if (request.getLevel() < 0 || request.getLevel() > 5) {
            throw new BadRequestException("Skill level must be between 0 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found in master dictionary: " + request.getSkillId()));

        if (Boolean.FALSE.equals(skill.getIsActive())) {
            throw new BadRequestException("Skill is currently inactive: " + request.getSkillId());
        }

        UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(userId, request.getSkillId())
                .orElseGet(() -> UserSkill.builder()
                        .id(UUID.randomUUID().toString())
                        .user(user)
                        .skill(skill)
                        .build());

        userSkill.setLevel(request.getLevel());
        UserSkill savedSkill = userSkillRepository.save(userSkill);

        // Recalculate user completion percentage
        user.setCompletionPercentage(completionCalculatorService.calculateCompletionPercentage(user));
        userRepository.save(user);

        return userProfileMapper.toSkillResponse(savedSkill);
    }
}
