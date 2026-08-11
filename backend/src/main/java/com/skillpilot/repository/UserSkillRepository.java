package com.skillpilot.repository;

import com.skillpilot.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, String> {
    List<UserSkill> findByUserId(String userId);
    Optional<UserSkill> findByUserIdAndSkillId(String userId, String skillId);
    void deleteByUserIdAndSkillId(String userId, String skillId);
}
