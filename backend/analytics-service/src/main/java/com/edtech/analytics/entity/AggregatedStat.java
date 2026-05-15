package com.edtech.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "aggregated_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedStat {
    @Id
    private String id; // e.g., course::<courseId> or user::<userId>

    @Column(nullable = false)
    private String scope; // COURSE or USER

    @Column
    private Long enrollments = 0L;

    @Column
    private Long completions = 0L;

    @Column
    private Long assessmentsSubmitted = 0L;

    @Column
    private Long assessmentsGraded = 0L;
}
