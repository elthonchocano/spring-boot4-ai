package com.echocano.ai.news.infrastructure.adapter.input;

import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import com.echocano.ai.news.application.port.input.GetNewsSummaryInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/news/summary")
@Validated
public class NewsResource implements INewsResource {

    private final GetNewsSummaryInputPort summaryInputPort;

    @Cacheable(value = "newsSummary", key = "#root.methodName + '_' + #country")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsSummaryResponse> getSummary(
            @RequestParam(defaultValue = "us") String country) {
        log.info("Current thread: {}", Thread.currentThread());
        return ResponseEntity.ok(summaryInputPort.getSummary(new NewsSummaryRequest(country)));
    }
}