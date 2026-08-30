package com.skillpilot.repository;

import com.skillpilot.entity.SavedCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedCareerRepository extends JpaRepository<SavedCareer, String> {

    List<SavedCareer> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<SavedCareer> findByUserIdAndCareerId(String userId, String careerId);

    boolean existsByUserIdAndCareerId(String userId, String careerId);

    void deleteByUserIdAndCareerId(String userId, String careerId);

    long countByUserId(String userId);
}
