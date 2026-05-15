package com.edtech.analytics.service;

import com.edtech.analytics.entity.AggregatedStat;
import com.edtech.analytics.entity.Event;
import com.edtech.analytics.repository.AggregatedStatRepository;
import com.edtech.analytics.repository.EventRepository;
import com.edtech.common.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class AnalyticsService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AggregatedStatRepository aggregatedStatRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "course-created", groupId = "analytics")
    public void onCourseCreated(String message) {
        processEvent("course-created", message);
    }

    @KafkaListener(topics = "course-published", groupId = "analytics")
    public void onCoursePublished(String message) {
        processEvent("course-published", message);
    }

    @KafkaListener(topics = "assessment-submitted", groupId = "analytics")
    public void onAssessmentSubmitted(String message) {
        processEvent("assessment-submitted", message);
    }

    @KafkaListener(topics = "assessment-graded", groupId = "analytics")
    public void onAssessmentGraded(String message) {
        processEvent("assessment-graded", message);
    }

    private void processEvent(String type, String payload) {
        try {
            // Persist raw event
            Event event = Event.builder().type(type).payload(payload).build();
            eventRepository.save(event);
            log.info("Event persisted: {}", type);

            // Update aggregates
            JsonNode node = objectMapper.readTree(payload);
            switch (type) {
                case "course-created":
                    handleCourseCreated(node);
                    break;
                case "course-published":
                    handleCoursePublished(node);
                    break;
                case "assessment-submitted":
                    handleAssessmentSubmitted(node);
                    break;
                case "assessment-graded":
                    handleAssessmentGraded(node);
                    break;
                default:
                    log.warn("Unhandled event type: {}", type);
            }
        } catch (Exception e) {
            log.error("Failed to process event {}", type, e);
            throw new BusinessException("Analytics processing error", 500, "ANALYTICS_ERROR");
        }
    }

    private void handleCourseCreated(JsonNode node) {
        // Example payload: {"courseId":"...","instructorId":"..."}
        if (!node.has("courseId")) return;
        String courseId = node.get("courseId").asText();
        String key = "course::" + courseId;
        AggregatedStat stat = aggregatedStatRepository.findById(key).orElse(AggregatedStat.builder().id(key).scope("COURSE").build());
        // course created doesn't change numeric stats but ensures record exists
        aggregatedStatRepository.save(stat);
    }

    private void handleCoursePublished(JsonNode node) {
        // Could mark published flag in aggregated stat if needed
        if (!node.has("courseId")) return;
        String courseId = node.get("courseId").asText();
        String key = "course::" + courseId;
        AggregatedStat stat = aggregatedStatRepository.findById(key).orElse(AggregatedStat.builder().id(key).scope("COURSE").build());
        aggregatedStatRepository.save(stat);
    }

    private void handleAssessmentSubmitted(JsonNode node) {
        // payload may include assessmentId, studentId, courseId
        if (!node.has("courseId")) return;
        String courseId = node.get("courseId").asText();
        String courseKey = "course::" + courseId;
        AggregatedStat courseStat = aggregatedStatRepository.findById(courseKey).orElse(AggregatedStat.builder().id(courseKey).scope("COURSE").build());
        courseStat.setAssessmentsSubmitted(courseStat.getAssessmentsSubmitted() == null ? 1L : courseStat.getAssessmentsSubmitted() + 1);
        aggregatedStatRepository.save(courseStat);

        if (node.has("studentId")) {
            String userKey = "user::" + node.get("studentId").asText();
            AggregatedStat userStat = aggregatedStatRepository.findById(userKey).orElse(AggregatedStat.builder().id(userKey).scope("USER").build());
            userStat.setAssessmentsSubmitted(userStat.getAssessmentsSubmitted() == null ? 1L : userStat.getAssessmentsSubmitted() + 1);
            aggregatedStatRepository.save(userStat);
        }
    }

    private void handleAssessmentGraded(JsonNode node) {
        if (!node.has("submissionId")) return;
        // submissionId may map to course/student via separate lookup; for simplicity, assume payload carries courseId and studentId
        if (!node.has("courseId")) return;
        String courseId = node.get("courseId").asText();
        String courseKey = "course::" + courseId;
        AggregatedStat courseStat = aggregatedStatRepository.findById(courseKey).orElse(AggregatedStat.builder().id(courseKey).scope("COURSE").build());
        courseStat.setAssessmentsGraded(courseStat.getAssessmentsGraded() == null ? 1L : courseStat.getAssessmentsGraded() + 1);
        aggregatedStatRepository.save(courseStat);

        if (node.has("studentId")) {
            String userKey = "user::" + node.get("studentId").asText();
            AggregatedStat userStat = aggregatedStatRepository.findById(userKey).orElse(AggregatedStat.builder().id(userKey).scope("USER").build());
            userStat.setAssessmentsGraded(userStat.getAssessmentsGraded() == null ? 1L : userStat.getAssessmentsGraded() + 1);
            aggregatedStatRepository.save(userStat);
        }
    }

    // Simple query helpers
    public AggregatedStat getStatsForCourse(String courseId) {
        String key = "course::" + courseId;
        return aggregatedStatRepository.findById(key).orElse(null);
    }

    public AggregatedStat getStatsForUser(String userId) {
        String key = "user::" + userId;
        return aggregatedStatRepository.findById(key).orElse(null);
    }
}
