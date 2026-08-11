package com.skillpilot.repository;

import com.skillpilot.entity.RoadmapPhaseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapPhaseTemplateRepository extends JpaRepository<RoadmapPhaseTemplate, String> {
    List<RoadmapPhaseTemplate> findByTemplateIdOrderByPhaseOrderAsc(String templateId);
}
