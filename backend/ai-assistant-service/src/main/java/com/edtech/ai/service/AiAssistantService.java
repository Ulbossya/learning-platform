package com.edtech.ai.service;

import com.edtech.ai.dto.ChatRequest;
import com.edtech.ai.dto.ChatResponse;
import com.edtech.ai.entity.AiSession;
import com.edtech.ai.repository.AiSessionRepository;
import com.edtech.common.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AiAssistantService {
    private final AiSessionRepository aiSessionRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String openAiUrl;

    @Value("${openai.model:gpt-4o}")
    private String model;

    public AiAssistantService(AiSessionRepository aiSessionRepository,
                              @Value("${openai.api.key:}") String openAiKey) {
        this.aiSessionRepository = aiSessionRepository;
        if (openAiKey == null || openAiKey.isBlank()) {
            this.webClient = WebClient.builder().build();
        } else {
            this.webClient = WebClient.builder()
                    .baseUrl("https://api.openai.com")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiKey)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }
    }

    public ChatResponse ask(ChatRequest request) {
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new BusinessException("Question is required", 400, "QUESTION_REQUIRED");
        }

        AiSession session = AiSession.builder()
                .userId(request.getUserId())
                .courseId(request.getCourseId())
                .question(request.getQuestion())
                .build();
        aiSessionRepository.save(session);

        try {
            // Build messages
            List<Object> messages = new ArrayList<>();
            messages.add(message("system", "You are an educational assistant. Answer concisely and cite sources when possible."));
            if (request.getContext() != null) {
                for (String ctx : request.getContext()) {
                    messages.add(message("user", "Context: " + ctx));
                }
            }
            messages.add(message("user", request.getQuestion()));

            // Build payload
            var payload = objectMapper.createObjectNode();
            payload.put("model", model);
            payload.set("messages", objectMapper.valueToTree(messages));
            payload.put("max_tokens", 800);
            payload.put("temperature", 0.2);

            Mono<String> resp = webClient.post()
                    .uri(openAiUrl.replace("https://api.openai.com", ""))
                    .body(BodyInserters.fromValue(payload))
                    .retrieve()
                    .bodyToMono(String.class);

            String body = resp.block();
            if (body == null) throw new RuntimeException("Empty response from OpenAI");

            JsonNode root = objectMapper.readTree(body);
            // Parse answer (compatibility with OpenAI response structure)
            String answer = "";
            if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                JsonNode choice = root.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    answer = choice.get("message").get("content").asText();
                } else if (choice.has("text")) {
                    answer = choice.get("text").asText();
                }
            }

            // Very simple source extraction (placeholder)
            List<String> sources = extractSources(answer);
            double confidence = estimateConfidence(answer);

            session.setAnswer(answer);
            session.setConfidence(confidence);
            aiSessionRepository.save(session);

            return new ChatResponse(answer, sources, confidence);
        } catch (Exception e) {
            log.error("AI call failed", e);
            throw new BusinessException("AI assistant error: " + e.getMessage(), 500, "AI_ERROR");
        }
    }

    private static Object message(String role, String content) {
        return new java.util.HashMap<String, String>() {{
            put("role", role);
            put("content", content);
        }};
    }

    private List<String> extractSources(String answer) {
        List<String> sources = new ArrayList<>();
        // naive: look for bracketed references [Module 1], (Chapter 3), URLs
        if (answer == null) return sources;
        var matcher = java.util.regex.Pattern.compile("\\[(.+?)\\]").matcher(answer);
        while (matcher.find()) sources.add(matcher.group(1));
        matcher = java.util.regex.Pattern.compile("https?://\\S+").matcher(answer);
        while (matcher.find()) sources.add(matcher.group());
        return sources;
    }

    private double estimateConfidence(String answer) {
        if (answer == null || answer.isBlank()) return 0.0;
        // placeholder heuristic
        double len = answer.length();
        if (len > 500) return 0.95;
        if (len > 200) return 0.9;
        if (len > 50) return 0.8;
        return 0.6;
    }
}
