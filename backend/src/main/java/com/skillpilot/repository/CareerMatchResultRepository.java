package com.skillpilot.repository;

import com.skillpilot.entity.CareerMatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerMatchResultRepository extends JpaRepository<CareerMatchResult, String> {

    List<CareerMatchResult> findByUserIdOrderByRankPositionAsc(String userId);

    Optional<CareerMatchResult> findByUserIdAndCareerId(String userId, String careerId);

    long countByCareerId(String careerId);

    void deleteByUserId(String userId);
}
