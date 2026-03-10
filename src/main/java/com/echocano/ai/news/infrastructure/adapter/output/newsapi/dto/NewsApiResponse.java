package com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsApiResponse {

    @JsonProperty("totalResults")
    private int totalResults;

    @JsonProperty("articles")
    private List<ArticleResponse> articles;
}
