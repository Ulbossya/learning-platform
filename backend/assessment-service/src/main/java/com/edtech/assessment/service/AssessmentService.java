package com.edtech.assessment.service;

import com.edtech.assessment.entity.Assessment;
import com.edtech.assessment.entity.Submission;
import com.edtech.assessment.repository.AssessmentRepository;
import com.edtech.assessment.repository.SubmissionRepository;
import com.edtech.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AssessmentService {
    @Autowired
    private AssessmentRepository assessmentRepository;
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public Assessment createAssessment(Assessment assessment) {
        log.info("Creating assessment: {}", assessment.getTitle());
        return assessmentRepository.save(assessment);
    }
    
    public Assessment getAssessment(String assessmentId) {
        return assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new BusinessException("Assessment not found", 404, "ASSESSMENT_NOT_FOUND"));
    }
    
    public List<Assessment> getCourseAssessments(String courseId) {
        return assessmentRepository.findByCourseId(courseId);
    }
    
    public Submission submitAssessment(String assessmentId, String studentId, Submission submission) {
        log.info("Submitting assessment: {} by student: {}", assessmentId, studentId);
        
        Assessment assessment = getAssessment(assessmentId);
        
        Submission existing = submissionRepository.findByAssessmentIdAndStudentId(assessmentId, studentId)
                .orElse(new Submission());
        
        existing.setAssessmentId(assessmentId);
        existing.setStudentId(studentId);
        existing.setSubmittedAt(LocalDateTime.now());
        existing.setStatus("SUBMITTED");
        
        Submission saved = submissionRepository.save(existing);
        kafkaTemplate.executeInTransaction(t -> t.send("assessment-submitted", assessmentId));
        
        return saved;
    }
    
    public void gradeSubmission(String submissionId, Double score) {
        log.info("Grading submission: {}", submissionId);
        
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException("Submission not found", 404, "SUBMISSION_NOT_FOUND"));
        
        submission.setScore(score);
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus("GRADED");
        
        submissionRepository.save(submission);
        kafkaTemplate.executeInTransaction(t -> t.send("assessment-graded", submissionId));
    }
    
    public List<Submission> getStudentSubmissions(String studentId) {
        return submissionRepository.findByStudentId(studentId);
    }
}