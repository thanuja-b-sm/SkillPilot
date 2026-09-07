package com.skillpilot.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String userRole;
    private UserProfileResponse userProfile;
    private Boolean requiresVerification;
    private String email;
    private String message;
}
