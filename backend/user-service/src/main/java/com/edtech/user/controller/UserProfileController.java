package com.edtech.user.controller;

import com.edtech.user.entity.UserProfile;
import com.edtech.user.service.UserProfileService;
import com.edtech.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Profile", description = "User profile management")
public class UserProfileController {
    @Autowired
    private UserProfileService userProfileService;
    
    @PostMapping
    @Operation(summary = "Create user profile")
    public ResponseEntity<ApiResponse<UserProfile>> createProfile(@RequestBody UserProfile profile) {
        UserProfile created = userProfileService.createProfile(profile);
        return new ResponseEntity<>(
                ApiResponse.success(created, "Profile created successfully"),
                HttpStatus.CREATED
        );
    }
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile")
    public ResponseEntity<ApiResponse<UserProfile>> getProfile(@PathVariable String userId) {
        UserProfile profile = userProfileService.getProfileById(userId);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile retrieved"));
    }
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse<UserProfile>> updateProfile(
            @PathVariable String userId,
            @RequestBody UserProfile profile) {
        UserProfile updated = userProfileService.updateProfile(userId, profile);
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated"));
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user profile")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable String userId) {
        userProfileService.deleteProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Profile deleted"));
    }
}