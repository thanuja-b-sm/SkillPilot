package com.skillpilot.repository;

import com.skillpilot.entity.AIGenerationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIGenerationLogRepository extends JpaRepository<AIGenerationLog, String> {
    List<AIGenerationLog> findByUserId(String userId);
}
