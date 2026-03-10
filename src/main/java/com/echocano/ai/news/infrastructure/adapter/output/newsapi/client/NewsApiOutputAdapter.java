package com.echocano.ai.news.infrastructure.adapter.output.newsapi.client;

import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiRequest;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiResponse;
import com.echocano.ai.news.infrastructure.port.output.ReadNewsOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class NewsApiOutputAdapter implements ReadNewsOutputPort {

    @Value("${api.news-api.key}")
    private String apiKey;

    @Value("${api.news-api.url.base}")
    private String baseUrl;

    @Value("${api.news-api.url.content}")
    private String headlines;

    @Override
    public NewsApiResponse getNews(NewsApiRequest request) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        return restClient.get().uri(headlines + "?country={country}&apiKey={apiKey}", request.getCountry(), apiKey)
                .retrieve()
                .body(NewsApiResponse.class);
    }
}
