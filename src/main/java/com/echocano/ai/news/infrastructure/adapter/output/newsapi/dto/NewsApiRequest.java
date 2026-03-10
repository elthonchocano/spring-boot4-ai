package com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class NewsApiRequest {
    private String country;
}
