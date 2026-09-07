package com.skillpilot.repository;

import com.skillpilot.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {

    Optional<EmailVerification> findFirstByEmailIgnoreCaseAndIsVerifiedFalseOrderByCreatedAtDesc(String email);

    Optional<EmailVerification> findFirstByUserIdAndIsVerifiedFalseOrderByCreatedAtDesc(String userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EmailVerification ev SET ev.isVerified = true WHERE LOWER(ev.email) = LOWER(:email) AND ev.isVerified = false")
    void invalidateAllActiveCodesForEmail(@Param("email") String email);
}
