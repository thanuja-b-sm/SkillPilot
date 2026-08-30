package com.skillpilot.repository;

import com.skillpilot.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, String> {

    List<PasswordResetCode> findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(String email);

    Optional<PasswordResetCode> findFirstByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(String email);

    @Modifying
    @Query("UPDATE PasswordResetCode p SET p.isUsed = true WHERE LOWER(p.email) = LOWER(:email) AND p.isUsed = false")
    void invalidateAllActiveCodesForEmail(@Param("email") String email);
}
