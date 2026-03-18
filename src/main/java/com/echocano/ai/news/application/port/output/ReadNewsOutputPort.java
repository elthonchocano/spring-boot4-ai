package com.echocano.ai.news.application.port.output;

import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiRequest;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiResponse;

public interface ReadNewsOutputPort {

    NewsApiResponse getNews(NewsApiRequest request);
}
