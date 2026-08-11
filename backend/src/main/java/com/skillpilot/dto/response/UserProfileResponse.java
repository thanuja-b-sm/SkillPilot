package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String title;
    private String education;
    private Integer experienceYears;
    private String location;
    private String targetFocus;
    private String bio;
    private Integer completionPercentage;
    private List<UserSkillResponse> skills;
}
