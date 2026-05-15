package com.edtech.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String userId;
    private String courseId;
    private String question;
    private List<String> context; // optional contextual snippets (titles/sections)
}
