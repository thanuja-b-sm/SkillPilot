package com.skillpilot.service;

import com.skillpilot.dto.request.ProfileUpdateRequest;
import com.skillpilot.dto.response.UserProfileResponse;
import com.skillpilot.entity.User;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    private final CompletionCalculatorService completionCalculatorService;

    public UserProfileService(
            UserRepository userRepository,
            UserProfileMapper userProfileMapper,
            CompletionCalculatorService completionCalculatorService) {
        this.userRepository = userRepository;
        this.userProfileMapper = userProfileMapper;
        this.completionCalculatorService = completionCalculatorService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + userId));
        return userProfileMapper.toProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateUserProfile(String userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with ID: " + userId));

        if (request != null) {
            if (request.getName() != null && !request.getName().isBlank()) {
                user.setName(request.getName().trim());
            }
            if (request.getTitle() != null) {
                user.setTitle(request.getTitle().trim());
            }
            if (request.getEducation() != null) {
                user.setEducation(request.getEducation().trim());
            }
            if (request.getLocation() != null) {
                user.setLocation(request.getLocation().trim());
            }
            if (request.getTargetFocus() != null) {
                user.setTargetFocus(request.getTargetFocus().trim());
            }
            if (request.getBio() != null) {
                user.setBio(request.getBio().trim());
            }
        }

        user.setCompletionPercentage(completionCalculatorService.calculateCompletionPercentage(user));
        User savedUser = userRepository.save(user);

        return userProfileMapper.toProfileResponse(savedUser);
    }
}
