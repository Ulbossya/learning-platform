package com.edtech.course.repository;

import com.edtech.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByInstructorId(String instructorId);
    List<Course> findByStatus(String status);
    List<Course> findByCategory(String category);
}