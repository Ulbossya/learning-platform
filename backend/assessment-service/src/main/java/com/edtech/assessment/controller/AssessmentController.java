package com.edtech.assessment.controller;

import com.edtech.assessment.entity.Assessment;
import com.edtech.assessment.entity.Submission;
import com.edtech.assessment.service.AssessmentService;
import com.edtech.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@Tag(name = "Assessments", description = "Assessment and grading")
public class AssessmentController {
    @Autowired
    private AssessmentService assessmentService;
    
    @PostMapping
    @Operation(summary = "Create assessment")
    public ResponseEntity<ApiResponse<Assessment>> createAssessment(@RequestBody Assessment assessment) {
        Assessment created = assessmentService.createAssessment(assessment);
        return new ResponseEntity<>(ApiResponse.success(created, "Assessment created"), HttpStatus.CREATED);
    }
    
    @GetMapping("/{assessmentId}")
    @Operation(summary = "Get assessment")
    public ResponseEntity<ApiResponse<Assessment>> getAssessment(@PathVariable String assessmentId) {
        Assessment assessment = assessmentService.getAssessment(assessmentId);
        return ResponseEntity.ok(ApiResponse.success(assessment, "Assessment retrieved"));
    }
    
    @PostMapping("/{assessmentId}/submit")
    @Operation(summary = "Submit assessment")
    public ResponseEntity<ApiResponse<Submission>> submitAssessment(
            @PathVariable String assessmentId,
            @RequestHeader("X-User-Id") String studentId,
            @RequestBody Submission submission) {
        Submission submitted = assessmentService.submitAssessment(assessmentId, studentId, submission);
        return new ResponseEntity<>(ApiResponse.success(submitted, "Assessment submitted"), HttpStatus.OK);
    }
    
    @PostMapping("/{submissionId}/grade")
    @Operation(summary = "Grade submission")
    public ResponseEntity<ApiResponse<Void>> gradeSubmission(
            @PathVariable String submissionId,
            @RequestParam Double score) {
        assessmentService.gradeSubmission(submissionId, score);
        return ResponseEntity.ok(ApiResponse.success(null, "Submission graded"));
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student submissions")
    public ResponseEntity<ApiResponse<List<Submission>>> getStudentSubmissions(@PathVariable String studentId) {
        List<Submission> submissions = assessmentService.getStudentSubmissions(studentId);
        return ResponseEntity.ok(ApiResponse.success(submissions, "Submissions retrieved"));
    }
}