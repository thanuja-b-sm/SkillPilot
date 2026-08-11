package com.skillpilot.repository;

import com.skillpilot.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    List<Question> findByIsActiveTrueOrderByDisplayOrderAsc();
    long countByIsActiveTrue();
}
