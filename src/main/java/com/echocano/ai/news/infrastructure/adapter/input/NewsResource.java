package com.echocano.ai.news.infrastructure.adapter.input;

import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import com.echocano.ai.news.infrastructure.port.input.GetNewsSummaryInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/news")
public class NewsResource {

    private final GetNewsSummaryInputPort summaryInputPort;

    @Cacheable(value = "newsSummary", key = "#root.methodName + '_' + #country")
    @GetMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsSummaryResponse> getSummary(
            @RequestParam(defaultValue = "us") String country) {
        return ResponseEntity.ok(summaryInputPort.getSummary(new NewsSummaryRequest(country)));
    }
}