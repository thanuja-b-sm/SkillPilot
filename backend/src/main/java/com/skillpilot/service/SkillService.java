package com.skillpilot.service;

import com.skillpilot.dto.request.SkillRequest;
import com.skillpilot.entity.Skill;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<Skill> getActiveSkills() {
        return skillRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public Skill getSkillById(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        if (Boolean.FALSE.equals(skill.getIsActive())) {
            throw new ResourceNotFoundException("Skill", "id", id);
        }

        return skill;
    }

    @Transactional(readOnly = true)
    public List<Skill> getAllSkillsAdmin() {
        return getAllSkillsAdmin(null, null);
    }

    @Transactional(readOnly = true)
    public List<Skill> getAllSkillsAdmin(String search, Boolean active) {
        return skillRepository.findAll().stream()
                .filter(s -> active == null || active.equals(s.getIsActive()))
                .filter(s -> search == null || search.isBlank() ||
                        (s.getName() != null && s.getName().toLowerCase().contains(search.toLowerCase())) ||
                        (s.getCategory() != null && s.getCategory().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Transactional
    public Skill activateSkill(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
        skill.setIsActive(true);
        return skillRepository.save(skill);
    }

    @Transactional
    public Skill createSkill(SkillRequest req) {
        String id = (req.getId() != null && !req.getId().isBlank()) ? req.getId() : UUID.randomUUID().toString();

        Skill skill = Skill.builder()
                .id(id)
                .name(req.getName())
                .category(req.getCategory())
                .description(req.getDescription())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build();

        return skillRepository.save(skill);
    }

    @Transactional
    public Skill updateSkill(String id, SkillRequest req) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        skill.setName(req.getName());
        skill.setCategory(req.getCategory());
        if (req.getDescription() != null) skill.setDescription(req.getDescription());
        if (req.getIsActive() != null) skill.setIsActive(req.getIsActive());

        return skillRepository.save(skill);
    }

    @Transactional
    public void deleteSkill(String id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));

        // Soft deactivation
        skill.setIsActive(false);
        skillRepository.save(skill);
    }
}
