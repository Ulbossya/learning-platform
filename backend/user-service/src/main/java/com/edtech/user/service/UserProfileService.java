package com.edtech.user.service;

import com.edtech.user.entity.UserProfile;
import com.edtech.user.repository.UserProfileRepository;
import com.edtech.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    public UserProfile createProfile(UserProfile profile) {
        log.info("Creating user profile for: {}", profile.getEmail());
        return userProfileRepository.save(profile);
    }
    
    public UserProfile getProfileById(String userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User profile not found", 404, "USER_NOT_FOUND"));
    }
    
    public UserProfile updateProfile(String userId, UserProfile profile) {
        log.info("Updating user profile: {}", userId);
        UserProfile existing = getProfileById(userId);
        
        if (profile.getFullName() != null) existing.setFullName(profile.getFullName());
        if (profile.getPhoneNumber() != null) existing.setPhoneNumber(profile.getPhoneNumber());
        if (profile.getAvatarUrl() != null) existing.setAvatarUrl(profile.getAvatarUrl());
        if (profile.getBio() != null) existing.setBio(profile.getBio());
        if (profile.getCountry() != null) existing.setCountry(profile.getCountry());
        if (profile.getCity() != null) existing.setCity(profile.getCity());
        
        return userProfileRepository.save(existing);
    }
    
    public void deleteProfile(String userId) {
        log.info("Deleting user profile: {}", userId);
        userProfileRepository.deleteById(userId);
    }
}