package com.edtech.analytics.controller;

import com.edtech.analytics.entity.AggregatedStat;
import com.edtech.analytics.service.AnalyticsService;
import com.edtech.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Analytics and aggregated statistics")
public class AnalyticsController {
    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get aggregated stats for a course")
    public ResponseEntity<ApiResponse<AggregatedStat>> getCourseStats(@PathVariable String courseId) {
        AggregatedStat stat = analyticsService.getStatsForCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(stat, "Course stats"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get aggregated stats for a user")
    public ResponseEntity<ApiResponse<AggregatedStat>> getUserStats(@PathVariable String userId) {
        AggregatedStat stat = analyticsService.getStatsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success(stat, "User stats"));
    }
}
