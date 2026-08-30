package com.skillpilot.service;

import com.skillpilot.dto.response.SavedCareerResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.SavedCareer;
import com.skillpilot.entity.User;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.SavedCareerRepository;
import com.skillpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedCareerService {

    private final SavedCareerRepository savedCareerRepository;
    private final CareerRepository careerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SavedCareerResponse> getSavedCareersForUser(String userId) {
        return savedCareerRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isCareerSaved(String userId, String careerId) {
        return savedCareerRepository.existsByUserIdAndCareerId(userId, careerId);
    }

    @Transactional
    public SavedCareerResponse saveCareer(String userId, String careerId, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", careerId));

        return savedCareerRepository.findByUserIdAndCareerId(userId, careerId)
                .map(existing -> {
                    if (notes != null) {
                        existing.setNotes(notes);
                        savedCareerRepository.save(existing);
                    }
                    return toResponse(existing);
                })
                .orElseGet(() -> {
                    SavedCareer newSaved = SavedCareer.builder()
                            .id(UUID.randomUUID().toString())
                            .user(user)
                            .career(career)
                            .notes(notes)
                            .build();
                    SavedCareer saved = savedCareerRepository.save(newSaved);
                    log.info("Career '{}' saved for user '{}' [SavedID: {}]", career.getTitle(), user.getEmail(), saved.getId());
                    return toResponse(saved);
                });
    }

    @Transactional
    public void unsaveCareer(String userId, String careerId) {
        if (savedCareerRepository.existsByUserIdAndCareerId(userId, careerId)) {
            savedCareerRepository.deleteByUserIdAndCareerId(userId, careerId);
            log.info("Career '{}' unsaved for user ID '{}'", careerId, userId);
        }
    }

    private SavedCareerResponse toResponse(SavedCareer sc) {
        Career c = sc.getCareer();
        return SavedCareerResponse.builder()
                .id(sc.getId())
                .careerId(c.getId())
                .title(c.getTitle())
                .category(c.getCategory())
                .description(c.getDescription())
                .averageSalary(c.getAverageSalary())
                .growthRate(c.getGrowthRate())
                .demandLevel(c.getDemandLevel() != null ? c.getDemandLevel().name() : "HIGH")
                .notes(sc.getNotes())
                .savedAt(sc.getCreatedAt())
                .build();
    }
}
