package com.edtech.course.service;

import com.edtech.course.entity.Course;
import com.edtech.course.repository.CourseRepository;
import com.edtech.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public Course createCourse(Course course) {
        log.info("Creating course: {}", course.getTitle());
        Course saved = courseRepository.save(course);
        kafkaTemplate.executeInTransaction(t -> t.send("course-created", saved.getId()));
        return saved;
    }
    
    public Course getCourse(String courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException("Course not found", 404, "COURSE_NOT_FOUND"));
    }
    
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public List<Course> getCoursesByInstructor(String instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }
    
    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatus("PUBLISHED");
    }
    
    public Course updateCourse(String courseId, Course course) {
        log.info("Updating course: {}", courseId);
        Course existing = getCourse(courseId);
        
        if (course.getTitle() != null) existing.setTitle(course.getTitle());
        if (course.getDescription() != null) existing.setDescription(course.getDescription());
        if (course.getCategory() != null) existing.setCategory(course.getCategory());
        if (course.getLevel() != null) existing.setLevel(course.getLevel());
        if (course.getStatus() != null) existing.setStatus(course.getStatus());
        
        return courseRepository.save(existing);
    }
    
    public void publishCourse(String courseId) {
        log.info("Publishing course: {}", courseId);
        Course course = getCourse(courseId);
        course.setStatus("PUBLISHED");
        courseRepository.save(course);
        kafkaTemplate.executeInTransaction(t -> t.send("course-published", courseId));
    }
    
    public void deleteCourse(String courseId) {
        log.info("Deleting course: {}", courseId);
        courseRepository.deleteById(courseId);
    }
}