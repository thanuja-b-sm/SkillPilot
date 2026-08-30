package com.skillpilot.service;

import com.skillpilot.dto.request.LoginRequest;
import com.skillpilot.dto.request.RegisterRequest;
import com.skillpilot.dto.response.AuthResponse;
import com.skillpilot.dto.response.UserProfileResponse;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.DuplicateResourceException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.exception.UnauthorizedException;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.security.JwtTokenProvider;
import com.skillpilot.security.SecurityUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserProfileMapper userProfileMapper;
    private final EmailService emailService;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{8,}$");

    private final com.skillpilot.repository.PasswordResetCodeRepository passwordResetCodeRepository;
    private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();
    private static final int MAX_RESET_ATTEMPTS = 5;

    @org.springframework.beans.factory.annotation.Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            AuthenticationManager authenticationManager,
            UserProfileMapper userProfileMapper,
            @org.springframework.beans.factory.annotation.Autowired(required = false) EmailService emailService,
            com.skillpilot.repository.PasswordResetCodeRepository passwordResetCodeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userProfileMapper = userProfileMapper;
        this.emailService = emailService;
        this.passwordResetCodeRepository = passwordResetCodeRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Registration details cannot be null");
        }

        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email address is already registered: " + email);
        }

        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            throw new BadRequestException("Password must be at least 8 characters long and contain at least one number and one uppercase letter");
        }

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.STUDENT) // Hardcoded STUDENT to prevent privilege escalation
                .title("Student Profile")
                .education(request.getEducation() != null ? request.getEducation().trim() : "Computer Science Senior")
                .experienceYears(0)
                .location("")
                .targetFocus(request.getTargetFocus() != null ? request.getTargetFocus().trim() : "Artificial Intelligence")
                .bio("")
                .completionPercentage(0)
                .build();

        User savedUser = userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities());

        String token = tokenProvider.generateToken(authentication);
        UserProfileResponse profileResponse = userProfileMapper.toProfileResponse(savedUser);

        return AuthResponse.builder()
                .token(token)
                .userRole(savedUser.getRole().getValue())
                .userProfile(profileResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new BadRequestException("Email and password are required");
        }

        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        String token = tokenProvider.generateToken(authentication);
        UserProfileResponse profileResponse = userProfileMapper.toProfileResponse(user);

        return AuthResponse.builder()
                .token(token)
                .userRole(user.getRole().getValue())
                .userProfile(profileResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user profile not found with ID: " + userId));
        return userProfileMapper.toProfileResponse(user);
    }


    @Transactional
    public com.skillpilot.dto.response.ForgotPasswordResponse forgotPassword(com.skillpilot.dto.request.ForgotPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email address is required");
        }

        String email = request.getEmail().trim().toLowerCase();
        java.util.Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);

        if (userOpt.isPresent()) {
            // Invalidate any existing active unused codes for this email
            passwordResetCodeRepository.invalidateAllActiveCodesForEmail(email);

            // Generate 6-digit cryptographically secure code
            String resetCode = String.format("%06d", SECURE_RANDOM.nextInt(1000000));
            java.time.LocalDateTime expiresAt = java.time.LocalDateTime.now().plusMinutes(15);

            com.skillpilot.entity.PasswordResetCode codeEntity = com.skillpilot.entity.PasswordResetCode.builder()
                    .id(UUID.randomUUID().toString())
                    .email(email)
                    .resetCode(resetCode)
                    .expiresAt(expiresAt)
                    .attemptsCount(0)
                    .isUsed(false)
                    .build();

            passwordResetCodeRepository.save(codeEntity);

            if (emailService != null) {
                emailService.sendPasswordResetEmail(email, resetCode);
            }
        }

        // Generic response — does not reveal whether the email exists (anti-enumeration)
        return com.skillpilot.dto.response.ForgotPasswordResponse.builder()
                .message("If an account with that email address is registered in SkillPilot, a 6-digit verification code has been sent.")
                .resetCode(null)
                .build();
    }

    @Transactional
    public com.skillpilot.dto.response.ForgotPasswordResponse resetPassword(com.skillpilot.dto.request.ResetPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getResetCode() == null || request.getNewPassword() == null) {
            throw new BadRequestException("Email, reset code, and new password are required");
        }

        String email = request.getEmail().trim().toLowerCase();
        com.skillpilot.entity.PasswordResetCode activeCode = passwordResetCodeRepository
                .findFirstByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification reset code. Please request a new code."));

        if (activeCode.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            activeCode.setIsUsed(true);
            passwordResetCodeRepository.save(activeCode);
            throw new BadRequestException("Verification reset code has expired. Please request a new code.");
        }

        if (activeCode.getAttemptsCount() >= MAX_RESET_ATTEMPTS) {
            activeCode.setIsUsed(true);
            passwordResetCodeRepository.save(activeCode);
            throw new BadRequestException("Maximum verification attempts exceeded. Please request a new verification code.");
        }

        if (!activeCode.getResetCode().equals(request.getResetCode().trim())) {
            activeCode.setAttemptsCount(activeCode.getAttemptsCount() + 1);
            if (activeCode.getAttemptsCount() >= MAX_RESET_ATTEMPTS) {
                activeCode.setIsUsed(true);
            }
            passwordResetCodeRepository.save(activeCode);
            if (activeCode.getIsUsed()) {
                throw new BadRequestException("Maximum verification attempts exceeded. Please request a new verification code.");
            }
            throw new BadRequestException("Invalid verification reset code.");
        }

        if (!PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            throw new BadRequestException("Password must be at least 8 characters long and contain at least one number and one uppercase letter");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark code as used
        activeCode.setIsUsed(true);
        passwordResetCodeRepository.save(activeCode);

        return com.skillpilot.dto.response.ForgotPasswordResponse.builder()
                .message("Password updated successfully. You can now log in with your new password.")
                .resetCode(null)
                .build();
    }
}

