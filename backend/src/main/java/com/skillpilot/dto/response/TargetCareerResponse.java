package com.skillpilot.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetCareerResponse {

    private String careerId;
    private String careerName;
    private LocalDateTime selectedAt;
    private Boolean active;
}
