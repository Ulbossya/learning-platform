package com.edtech.course.controller;

import com.edtech.course.entity.Course;
import com.edtech.course.service.CourseService;
import com.edtech.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Courses", description = "Course management")
public class CourseController {
    @Autowired
    private CourseService courseService;
    
    @PostMapping
    @Operation(summary = "Create new course")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        Course created = courseService.createCourse(course);
        return new ResponseEntity<>(ApiResponse.success(created, "Course created"), HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success(courses, "Courses retrieved"));
    }
    
    @GetMapping("/{courseId}")
    @Operation(summary = "Get course details")
    public ResponseEntity<ApiResponse<Course>> getCourse(@PathVariable String courseId) {
        Course course = courseService.getCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(course, "Course retrieved"));
    }
    
    @PutMapping("/{courseId}")
    @Operation(summary = "Update course")
    public ResponseEntity<ApiResponse<Course>> updateCourse(
            @PathVariable String courseId,
            @RequestBody Course course) {
        Course updated = courseService.updateCourse(courseId, course);
        return ResponseEntity.ok(ApiResponse.success(updated, "Course updated"));
    }
    
    @PostMapping("/{courseId}/publish")
    @Operation(summary = "Publish course")
    public ResponseEntity<ApiResponse<Void>> publishCourse(@PathVariable String courseId) {
        courseService.publishCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(null, "Course published"));
    }
    
    @DeleteMapping("/{courseId}")
    @Operation(summary = "Delete course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(null, "Course deleted"));
    }
}