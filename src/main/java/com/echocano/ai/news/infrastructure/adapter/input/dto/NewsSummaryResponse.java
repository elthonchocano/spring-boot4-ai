package com.echocano.ai.news.infrastructure.adapter.input.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
public class NewsSummaryResponse {

    private String countryCode;
    private LocalDate date;
    private String summary;
}
