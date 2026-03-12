package com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ArticleResponse(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("content") String content
) {
}
