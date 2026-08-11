package com.skillpilot.repository;

import com.skillpilot.entity.RoadmapMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapMilestoneRepository extends JpaRepository<RoadmapMilestone, String> {
    List<RoadmapMilestone> findByRoadmapIdOrderByPhaseOrderAsc(String roadmapId);
}
