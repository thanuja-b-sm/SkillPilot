package com.skillpilot.repository;

import com.skillpilot.entity.QuestionSkillMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionSkillMappingRepository extends JpaRepository<QuestionSkillMapping, String> {
    List<QuestionSkillMapping> findByOptionId(String optionId);
}
