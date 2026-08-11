package com.skillpilot.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillResponse {
    private String skillId;
    private String name;
    private String category;
    private Integer level;
}
