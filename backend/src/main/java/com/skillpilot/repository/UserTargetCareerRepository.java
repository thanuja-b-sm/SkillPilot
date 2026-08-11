package com.skillpilot.repository;

import com.skillpilot.entity.UserTargetCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTargetCareerRepository extends JpaRepository<UserTargetCareer, String> {
    Optional<UserTargetCareer> findByUserId(String userId);
}
