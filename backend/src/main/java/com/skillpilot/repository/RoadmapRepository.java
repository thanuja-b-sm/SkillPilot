package com.skillpilot.repository;

import com.skillpilot.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, String> {
    Optional<Roadmap> findByUserIdAndCareerId(String userId, String careerId);

    long countByCareerId(String careerId);
}
