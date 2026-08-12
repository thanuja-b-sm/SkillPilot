package com.skillpilot.service;

import com.skillpilot.dto.request.CareerRequest;
import com.skillpilot.dto.response.CareerRequirementResponse;
import com.skillpilot.dto.response.CareerResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.DemandLevel;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.SkillRepository;
import com.skillpilot.repository.CareerSkillRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final SkillRepository skillRepository;
    private final CareerSkillRequirementRepository requirementRepository;
    private final CareerMapper careerMapper;

    @Transactional(readOnly = true)
    public List<CareerResponse> getActiveCareers() {
        return careerRepository.findByIsActiveTrue().stream()
                .map(careerMapper::toCareerResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CareerResponse getCareerById(String id) {
        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", id));

        if (Boolean.FALSE.equals(career.getIsActive())) {
            throw new ResourceNotFoundException("Career", "id", id);
        }

        return careerMapper.toCareerResponse(career);
    }

    @Transactional(readOnly = true)
    public List<CareerRequirementResponse> getSkillsForCareer(String careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", careerId));
        if (Boolean.FALSE.equals(career.getIsActive())) {
            throw new ResourceNotFoundException("Career", "id", careerId);
        }
        return requirementRepository.findByCareerId(careerId).stream()
                .filter(req -> req.getSkill() != null && Boolean.TRUE.equals(req.getSkill().getIsActive()))
                .map(careerMapper::toCareerRequirementResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CareerResponse> getAllCareersAdmin() {
        return getAllCareersAdmin(null, null);
    }

    @Transactional(readOnly = true)
    public List<CareerResponse> getAllCareersAdmin(String search, Boolean active) {
        return careerRepository.findAll().stream()
                .filter(c -> active == null || active.equals(c.getIsActive()))
                .filter(c -> search == null || search.isBlank() ||
                        (c.getTitle() != null && c.getTitle().toLowerCase().contains(search.toLowerCase())) ||
                        (c.getCategory() != null && c.getCategory().toLowerCase().contains(search.toLowerCase())))
                .map(careerMapper::toCareerResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CareerResponse activateCareer(String id) {
        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", id));
        career.setIsActive(true);
        Career saved = careerRepository.save(career);
        return careerMapper.toCareerResponse(saved);
    }

    @Transactional
    public CareerResponse createCareer(CareerRequest req) {
        String id = (req.getId() != null && !req.getId().isBlank()) ? req.getId() : UUID.randomUUID().toString();

        Career career = Career.builder()
                .id(id)
                .title(req.getTitle())
                .category(req.getCategory())
                .description(req.getDescription())
                .averageSalary(req.getAverageSalary())
                .growthRate(req.getGrowthRate())
                .demandLevel(parseDemandLevel(req.getDemandLevel()))
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .typicalRoles(req.getTypicalRoles() != null ? new ArrayList<>(req.getTypicalRoles()) : new ArrayList<>())
                .recommendedPrerequisites(req.getRecommendedPrerequisites() != null ? new ArrayList<>(req.getRecommendedPrerequisites()) : new ArrayList<>())
                .build();

        Career saved = careerRepository.save(career);
        return careerMapper.toCareerResponse(saved);
    }

    @Transactional
    public CareerResponse updateCareer(String id, CareerRequest req) {
        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", id));

        career.setTitle(req.getTitle());
        career.setCategory(req.getCategory());

        if (req.getDescription() != null) career.setDescription(req.getDescription());
        if (req.getAverageSalary() != null) career.setAverageSalary(req.getAverageSalary());
        if (req.getGrowthRate() != null) career.setGrowthRate(req.getGrowthRate());
        if (req.getDemandLevel() != null) career.setDemandLevel(parseDemandLevel(req.getDemandLevel()));
        if (req.getIsActive() != null) career.setIsActive(req.getIsActive());

        if (req.getTypicalRoles() != null) {
            career.setTypicalRoles(new ArrayList<>(req.getTypicalRoles()));
        }
        if (req.getRecommendedPrerequisites() != null) {
            career.setRecommendedPrerequisites(new ArrayList<>(req.getRecommendedPrerequisites()));
        }

        Career saved = careerRepository.save(career);
        return careerMapper.toCareerResponse(saved);
    }

    @Transactional
    public void deleteCareer(String id) {
        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", id));

        // Soft deactivation to preserve historical match records
        career.setIsActive(false);
        careerRepository.save(career);
    }

    @Transactional
    public CareerResponse addRequirement(String careerId, com.skillpilot.dto.request.CareerSkillRequirementRequest req) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", careerId));
        com.skillpilot.entity.Skill skill = skillRepository.findById(req.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", req.getSkillId()));

        if (req.getRequiredLevel() < 1 || req.getRequiredLevel() > 5) {
            throw new com.skillpilot.exception.BadRequestException("Required skill level must be between 1 and 5");
        }

        com.skillpilot.entity.CareerSkillRequirement reqEntity = requirementRepository.findByCareerIdAndSkillId(careerId, req.getSkillId())
                .orElseGet(() -> com.skillpilot.entity.CareerSkillRequirement.builder()
                        .id(UUID.randomUUID().toString())
                        .career(career)
                        .skill(skill)
                        .build());

        reqEntity.setRequiredLevel(req.getRequiredLevel());
        reqEntity.setIsEssential(req.getIsEssential() != null ? req.getIsEssential() : false);
        requirementRepository.save(reqEntity);

        return careerMapper.toCareerResponse(careerRepository.findById(careerId).orElse(career));
    }

    @Transactional
    public CareerResponse updateRequirement(String requirementId, com.skillpilot.dto.request.CareerSkillRequirementRequest req) {
        com.skillpilot.entity.CareerSkillRequirement reqEntity = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("CareerRequirement", "id", requirementId));

        if (req.getRequiredLevel() != null) {
            if (req.getRequiredLevel() < 1 || req.getRequiredLevel() > 5) {
                throw new com.skillpilot.exception.BadRequestException("Required skill level must be between 1 and 5");
            }
            reqEntity.setRequiredLevel(req.getRequiredLevel());
        }
        if (req.getIsEssential() != null) {
            reqEntity.setIsEssential(req.getIsEssential());
        }

        requirementRepository.save(reqEntity);
        return careerMapper.toCareerResponse(reqEntity.getCareer());
    }

    @Transactional
    public void deleteRequirement(String requirementId) {
        com.skillpilot.entity.CareerSkillRequirement reqEntity = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new ResourceNotFoundException("CareerRequirement", "id", requirementId));
        requirementRepository.delete(reqEntity);
    }

    private DemandLevel parseDemandLevel(String val) {
        if (val == null || val.isBlank()) return DemandLevel.HIGH;
        for (DemandLevel dl : DemandLevel.values()) {
            if (dl.name().equalsIgnoreCase(val) || dl.getValue().equalsIgnoreCase(val)) {
                return dl;
            }
        }
        return DemandLevel.HIGH;
    }
}
