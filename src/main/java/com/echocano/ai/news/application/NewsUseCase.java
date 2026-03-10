package com.echocano.ai.news.application;

import com.echocano.ai.news.application.mapper.NewsMapper;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiRequest;
import com.echocano.ai.news.infrastructure.port.input.GetNewsSummaryInputPort;
import com.echocano.ai.news.infrastructure.port.output.ReadNewsOutputPort;
import com.echocano.ai.news.infrastructure.port.output.SummaryOutputPort;
import com.echocano.ai.news.domain.News;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsUseCase implements GetNewsSummaryInputPort {

    @Value("${prompt}")
    private String prompt;

    private final ReadNewsOutputPort readNewsOutputPort;
    private final SummaryOutputPort summaryOutputPort;
    private final NewsMapper newsMapper;

    @Override
    public NewsSummaryResponse getSummary(NewsSummaryRequest newsSummaryRequest) {
        List<News> newsList = newsMapper.toDomain(readNewsOutputPort.getNews(NewsApiRequest.builder()
                .country(newsSummaryRequest.countryCode()).build()).getArticles());
        StringBuilder content = new StringBuilder();
        content.append(prompt);
        for (News news : newsList) {
            content.append("\nTitle: ").append(news.title());
            content.append("\nDescription: ").append(news.content());
            content.append("\nEnd of article\n");
        }
        String summary = summaryOutputPort.getSummary(content.toString());
        return NewsSummaryResponse.builder()
                .date(LocalDate.now())
                .countryCode(newsSummaryRequest.countryCode())
                .summary(summary)
                .build();
    }
}
