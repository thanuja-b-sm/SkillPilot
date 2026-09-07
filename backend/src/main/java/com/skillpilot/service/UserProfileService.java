package com.skillpilot.service;

import com.skillpilot.dto.request.ProfileUpdateRequest;
import com.skillpilot.dto.response.UserProfileResponse;
import com.skillpilot.entity.User;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
            if (request.getTitle() != null) user.setTitle(request.getTitle().trim());
            if (request.getEducation() != null) user.setEducation(request.getEducation().trim());
            if (request.getInstitutionName() != null) user.setInstitutionName(request.getInstitutionName().trim());
            if (request.getDegreeLevel() != null) user.setDegreeLevel(request.getDegreeLevel().trim());
            if (request.getMajorFieldOfStudy() != null) user.setMajorFieldOfStudy(request.getMajorFieldOfStudy().trim());
            if (request.getGraduationYear() != null) user.setGraduationYear(request.getGraduationYear());
            if (request.getEducationStatus() != null) user.setEducationStatus(request.getEducationStatus().trim());

            if (request.getExperienceYears() != null) user.setExperienceYears(request.getExperienceYears());
            if (request.getEmploymentStatus() != null) user.setEmploymentStatus(request.getEmploymentStatus().trim());
            if (request.getCurrentJobTitle() != null) user.setCurrentJobTitle(request.getCurrentJobTitle().trim());
            if (request.getCurrentIndustry() != null) user.setCurrentIndustry(request.getCurrentIndustry().trim());
            if (request.getRelevantExperienceYears() != null) user.setRelevantExperienceYears(request.getRelevantExperienceYears());

            if (request.getLocation() != null) user.setLocation(request.getLocation().trim());
            if (request.getCountry() != null) user.setCountry(request.getCountry().trim());
            if (request.getDateOfBirth() != null) {
                String dobStr = request.getDateOfBirth().trim();
                if (dobStr.isEmpty()) {
                    user.setDateOfBirth(null);
                } else {
                    LocalDate dob;
                    try {
                        dob = LocalDate.parse(dobStr);
                    } catch (DateTimeParseException e) {
                        throw new BadRequestException("Invalid date format for date of birth. Please use YYYY-MM-DD.");
                    }
                    LocalDate now = LocalDate.now();
                    if (dob.isAfter(now)) {
                        throw new BadRequestException("Date of birth cannot be in the future");
                    }
                    if (dob.isBefore(now.minusYears(120)) || dob.isAfter(now.minusYears(13))) {
                        throw new BadRequestException("Please enter a valid date of birth (age must be between 13 and 120 years)");
                    }
                    user.setDateOfBirth(dob);
                }
            }

            if (request.getTargetFocus() != null) user.setTargetFocus(request.getTargetFocus().trim());
            if (request.getPreferredWorkMode() != null) user.setPreferredWorkMode(request.getPreferredWorkMode().trim());
            if (request.getPreferredEmploymentType() != null) user.setPreferredEmploymentType(request.getPreferredEmploymentType().trim());
            if (request.getCareerGoal() != null) user.setCareerGoal(request.getCareerGoal().trim());

            if (request.getWeeklyHoursAvailable() != null) user.setWeeklyHoursAvailable(request.getWeeklyHoursAvailable());
            if (request.getPreferredLearningPace() != null) user.setPreferredLearningPace(request.getPreferredLearningPace().trim());
            if (request.getPreferredRoadmapDuration() != null) user.setPreferredRoadmapDuration(request.getPreferredRoadmapDuration());

            if (request.getBio() != null) user.setBio(request.getBio().trim());
            if (request.getCertifications() != null) user.setCertifications(request.getCertifications().trim());
            if (request.getPortfolioUrl() != null) user.setPortfolioUrl(request.getPortfolioUrl().trim());
            if (request.getCareerInterests() != null) user.setCareerInterests(request.getCareerInterests().trim());
        }

        user.setCompletionPercentage(completionCalculatorService.calculateCompletionPercentage(user));
        User savedUser = userRepository.save(user);

        return userProfileMapper.toProfileResponse(savedUser);
    }
}
