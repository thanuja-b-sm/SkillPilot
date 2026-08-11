package com.skillpilot.repository;

import com.skillpilot.entity.UserQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuestionAnswerRepository extends JpaRepository<UserQuestionAnswer, String> {
    List<UserQuestionAnswer> findByUserId(String userId);
    Optional<UserQuestionAnswer> findByUserIdAndQuestionId(String userId, String questionId);
}
