package com.edtech.ai.controller;

import com.edtech.ai.dto.ChatRequest;
import com.edtech.ai.dto.ChatResponse;
import com.edtech.ai.service.AiAssistantService;
import com.edtech.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI Assistant", description = "AI chat assistant for course materials")
public class AiController {
    @Autowired
    private AiAssistantService aiAssistantService;

    @PostMapping("/chat")
    @Operation(summary = "Ask AI assistant a question")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = aiAssistantService.ask(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Answer generated"), HttpStatus.OK);
    }
}
