package com.edtech.content.controller;

import com.edtech.content.entity.Content;
import com.edtech.content.service.ContentService;
import com.edtech.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@Tag(name = "Content", description = "Learning content management")
public class ContentController {
    @Autowired
    private ContentService contentService;
    
    @PostMapping
    @Operation(summary = "Add content to course")
    public ResponseEntity<ApiResponse<Content>> addContent(@RequestBody Content content) {
        Content created = contentService.addContent(content);
        return new ResponseEntity<>(ApiResponse.success(created, "Content added"), HttpStatus.CREATED);
    }
    
    @GetMapping("/{contentId}")
    @Operation(summary = "Get content details")
    public ResponseEntity<ApiResponse<Content>> getContent(@PathVariable String contentId) {
        Content content = contentService.getContent(contentId);
        return ResponseEntity.ok(ApiResponse.success(content, "Content retrieved"));
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all course materials")
    public ResponseEntity<ApiResponse<List<Content>>> getCourseMaterials(@PathVariable String courseId) {
        List<Content> materials = contentService.getCourseMaterials(courseId);
        return ResponseEntity.ok(ApiResponse.success(materials, "Materials retrieved"));
    }
    
    @PutMapping("/{contentId}")
    @Operation(summary = "Update content")
    public ResponseEntity<ApiResponse<Content>> updateContent(
            @PathVariable String contentId,
            @RequestBody Content content) {
        Content updated = contentService.updateContent(contentId, content);
        return ResponseEntity.ok(ApiResponse.success(updated, "Content updated"));
    }
    
    @DeleteMapping("/{contentId}")
    @Operation(summary = "Delete content")
    public ResponseEntity<ApiResponse<Void>> deleteContent(
            @PathVariable String contentId,
            @RequestParam String courseId) {
        contentService.deleteContent(contentId, courseId);
        return ResponseEntity.ok(ApiResponse.success(null, "Content deleted"));
    }
}