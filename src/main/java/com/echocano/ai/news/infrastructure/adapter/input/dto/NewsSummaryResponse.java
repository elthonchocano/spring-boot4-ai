package com.echocano.ai.news.infrastructure.adapter.input.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "NewsSummaryResponse", description = "Summary respond")
public class NewsSummaryResponse {

    @Schema(example = "us")
    private String countryCode;
    @Schema(example = "2026-03-15T12:00:00")
    private LocalDate date;
    @Schema(example = "1. Jürgen Habermas, a renowned German philosopher, passes away at the age of 96.")
    private String summary;
}
