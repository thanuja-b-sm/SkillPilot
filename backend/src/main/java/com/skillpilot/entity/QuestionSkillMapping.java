package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_skill_mappings", uniqueConstraints = {
    @UniqueConstraint(name = "uq_option_skill", columnNames = {"option_id", "skill_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionSkillMapping {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private QuestionOption option;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false)
    @Builder.Default
    private Integer weight = 1; // 1 to 5
}
