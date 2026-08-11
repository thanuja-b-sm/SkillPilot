package com.skillpilot.repository;

import com.skillpilot.entity.RoadmapTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoadmapTemplateRepository extends JpaRepository<RoadmapTemplate, String> {
    Optional<RoadmapTemplate> findByCareerId(String careerId);
}
