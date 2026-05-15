package com.edtech.assessment.repository;

import com.edtech.assessment.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    List<Submission> findByAssessmentId(String assessmentId);
    Optional<Submission> findByAssessmentIdAndStudentId(String assessmentId, String studentId);
    List<Submission> findByStudentId(String studentId);
}