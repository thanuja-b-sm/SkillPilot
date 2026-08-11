package com.skillpilot.repository;

import com.skillpilot.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CareerRepository extends JpaRepository<Career, String> {
    List<Career> findByIsActiveTrue();
    long countByIsActiveTrue();
    Optional<Career> findByTitle(String title);
}
