package com.echocano.ai.news.application;

import com.echocano.ai.news.application.mapper.NewsMapper;
import com.echocano.ai.news.domain.News;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiResponse;
import com.echocano.ai.news.infrastructure.port.output.ReadNewsOutputPort;
import com.echocano.ai.news.infrastructure.port.output.SummaryOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class NewsUseCaseTest {

    private final String testPrompt = "Please summarize the following news:";
    @Mock
    private ReadNewsOutputPort readNewsOutputPort;
    @Mock
    private SummaryOutputPort summaryOutputPort;
    @Mock
    private NewsMapper newsMapper;
    @InjectMocks
    private NewsUseCase useCase;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(useCase, "prompt", testPrompt);
    }

    @Test
    @DisplayName("Should return a summary")
    void test1() {
        // GIVEN
        String countryCode = "CA";
        NewsSummaryRequest request = new NewsSummaryRequest(countryCode);

        NewsApiResponse apiResponse = new NewsApiResponse();
        Mockito.when(readNewsOutputPort.getNews(ArgumentMatchers.any())).thenReturn(apiResponse);

        News newsItem = new News("Spring Boot 4", "Everything is resilient now.");
        Mockito.when(newsMapper.toDomain(ArgumentMatchers.any())).thenReturn(List.of(newsItem));

        String expectedSummary = "AI Summary Result";
        Mockito.when(summaryOutputPort.getSummary(ArgumentMatchers.anyString())).thenReturn(expectedSummary);

        // WHEN
        NewsSummaryResponse response = useCase.getSummary(request);

        // THEN
        assertNotNull(response);
        assertEquals(expectedSummary, response.getSummary());
        assertEquals(countryCode, response.getCountryCode());
        assertEquals(LocalDate.now(), response.getDate());

        Mockito.verify(summaryOutputPort).getSummary(ArgumentMatchers.contains(testPrompt));
        Mockito.verify(summaryOutputPort).getSummary(ArgumentMatchers.contains("Spring Boot 4"));
    }
}