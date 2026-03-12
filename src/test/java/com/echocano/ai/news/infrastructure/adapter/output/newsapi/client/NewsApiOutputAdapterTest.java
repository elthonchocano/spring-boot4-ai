package com.echocano.ai.news.infrastructure.adapter.output.newsapi.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class NewsApiOutputAdapterTest {

    @Value("${api.news-api.url.base}")
    private String baseUrl;

    @Value("${api.news-api.url.content}")
    private String headlines;

    @Autowired
    private RestTestClient restClient;

    @Test
    void testExternalApiCall() {
        restClient.get().uri(baseUrl+headlines)
                .exchange()
                .expectStatus().isOk();
    }
}