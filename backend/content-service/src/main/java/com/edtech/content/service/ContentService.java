package com.edtech.content.service;

import com.edtech.content.entity.Content;
import com.edtech.content.repository.ContentRepository;
import com.edtech.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ContentService {
    @Autowired
    private ContentRepository contentRepository;
    
    public Content addContent(Content content) {
        log.info("Adding content to course: {}", content.getCourseId());
        return contentRepository.save(content);
    }
    
    @Cacheable(value = "content", key = "#contentId")
    public Content getContent(String contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException("Content not found", 404, "CONTENT_NOT_FOUND"));
    }
    
    @Cacheable(value = "courseMaterials", key = "#courseId")
    public List<Content> getCourseMaterials(String courseId) {
        return contentRepository.findByCourseIdOrderByOrderNumber(courseId);
    }
    
    @CacheEvict(value = "courseMaterials", key = "#courseId")
    public Content updateContent(String contentId, Content content) {
        log.info("Updating content: {}", contentId);
        Content existing = getContent(contentId);
        
        if (content.getTitle() != null) existing.setTitle(content.getTitle());
        if (content.getDescription() != null) existing.setDescription(content.getDescription());
        if (content.getContentUrl() != null) existing.setContentUrl(content.getContentUrl());
        if (content.getOrderNumber() != null) existing.setOrderNumber(content.getOrderNumber());
        
        return contentRepository.save(existing);
    }
    
    @CacheEvict(value = "courseMaterials", key = "#courseId")
    public void deleteContent(String contentId, String courseId) {
        log.info("Deleting content: {}", contentId);
        contentRepository.deleteById(contentId);
    }
}