package com.edtech.assessment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String assessmentId;
    
    @Column(nullable = false)
    private String studentId;
    
    @Column
    private Double score;
    
    @Column
    private LocalDateTime submittedAt;
    
    @Column
    private LocalDateTime gradedAt;
    
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, GRADED, SUBMITTED
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}