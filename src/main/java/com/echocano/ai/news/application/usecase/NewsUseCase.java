package com.echocano.ai.news.application.usecase;

import com.echocano.ai.news.application.mapper.NewsMapper;
import com.echocano.ai.news.application.port.input.GetNewsSummaryInputPort;
import com.echocano.ai.news.application.port.output.ReadNewsOutputPort;
import com.echocano.ai.news.application.port.output.SummaryOutputPort;
import com.echocano.ai.news.domain.News;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsUseCase implements GetNewsSummaryInputPort {

    private final ReadNewsOutputPort readNewsOutputPort;
    private final SummaryOutputPort summaryOutputPort;
    private final NewsMapper newsMapper;
    @Value("classpath:/prompts/news-summary.st")
    private Resource promptResource;

    @Override
    public NewsSummaryResponse getSummary(NewsSummaryRequest newsSummaryRequest) {
        List<News> newsList = newsMapper.toDomain(readNewsOutputPort.getNews(NewsApiRequest.builder()
                .country(newsSummaryRequest.countryCode()).build()).getArticles());
        PromptTemplate template = new PromptTemplate(promptResource);
        StringBuilder content = new StringBuilder();
        for (News news : newsList) {
            content.append("\nTitle: ").append(news.title());
            content.append("\nDescription: ").append(news.content());
            content.append("\nEnd of article\n");
        }
        Prompt prompt = template.create(Map.of("headlines", content));
        String summary = summaryOutputPort.getSummary(prompt.toString());
        return NewsSummaryResponse.builder()
                .date(LocalDate.now())
                .countryCode(newsSummaryRequest.countryCode())
                .summary(summary)
                .build();
    }
}
