package com.echocano.ai.news.infrastructure.adapter.output.ollama.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaResponse {
    private String model;
    private String response;
}
