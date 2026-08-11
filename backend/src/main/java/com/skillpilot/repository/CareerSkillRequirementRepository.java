package com.skillpilot.repository;

import com.skillpilot.entity.CareerSkillRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerSkillRequirementRepository extends JpaRepository<CareerSkillRequirement, String> {
    List<CareerSkillRequirement> findByCareerId(String careerId);
    Optional<CareerSkillRequirement> findByCareerIdAndSkillId(String careerId, String skillId);
}
