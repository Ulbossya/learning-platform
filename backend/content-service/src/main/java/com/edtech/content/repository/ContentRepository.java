package com.edtech.content.repository;

import com.edtech.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, String> {
    List<Content> findByCourseId(String courseId);
    List<Content> findByCourseIdOrderByOrderNumber(String courseId);
}