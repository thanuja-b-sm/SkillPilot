package com.skillpilot.config;

import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.service.CompletionCalculatorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompletionCalculatorService completionCalculatorService;

    public DefaultUserSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CompletionCalculatorService completionCalculatorService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.completionCalculatorService = completionCalculatorService;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) {
        // Seed or update default student account
        userRepository.findByEmail("alex.rivera@university.edu").ifPresentOrElse(
            student -> {
                student.setPasswordHash(passwordEncoder.encode("Password123"));
                student.setRole(UserRole.STUDENT);
                student.setCompletionPercentage(completionCalculatorService.calculateCompletionPercentage(student));
                userRepository.save(student);
            },
            () -> {
                User student = User.builder()
                        .id("user-student-alex")
                        .name("Alex Rivera")
                        .email("alex.rivera@university.edu")
                        .passwordHash(passwordEncoder.encode("Password123"))
                        .role(UserRole.STUDENT)
                        .title("Computer Science Senior")
                        .education("B.S. Computer Science")
                        .experienceYears(1)
                        .location("San Francisco, CA")
                        .targetFocus("Artificial Intelligence")
                        .bio("Senior CS student specializing in machine learning algorithms and software engineering.")
                        .completionPercentage(65)
                        .build();
                student.setCompletionPercentage(completionCalculatorService.calculateCompletionPercentage(student));
                userRepository.save(student);
            }
        );

        // Seed or update default admin account
        userRepository.findByEmail("admin@skillpilot.com").ifPresentOrElse(
            admin -> {
                admin.setPasswordHash(passwordEncoder.encode("AdminPassword123"));
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
            },
            () -> {
                User admin = User.builder()
                        .id("user-admin-root")
                        .name("System Administrator")
                        .email("admin@skillpilot.com")
                        .passwordHash(passwordEncoder.encode("AdminPassword123"))
                        .role(UserRole.ADMIN)
                        .title("Platform Administrator")
                        .education("M.S. Software Engineering")
                        .experienceYears(10)
                        .location("Austin, TX")
                        .targetFocus("System Administration")
                        .bio("System administrator responsible for dataset management and algorithmic weights configuration.")
                        .completionPercentage(100)
                        .build();
                userRepository.save(admin);
            }
        );
    }
}
