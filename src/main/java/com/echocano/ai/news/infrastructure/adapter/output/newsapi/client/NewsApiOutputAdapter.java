package com.echocano.ai.news.infrastructure.adapter.output.newsapi.client;

import com.echocano.ai.news.application.exceptions.ApiNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiRequest;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiResponse;
import com.echocano.ai.news.infrastructure.port.output.ReadNewsOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
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
        NewsApiResponse response;
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        try {
            response = restClient.get().uri(headlines + "?country={country}&apiKey={apiKey}", request.getCountry(), apiKey)
                    .retrieve()
                    .body(NewsApiResponse.class);
        } catch (ResourceAccessException e) {
            throw new ApiNotAvailableException(String.format(
                    "Api %s is not available at this moment", baseUrl));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotDefineException(e.getMessage());
        }
        return response;
    }
}
