package com.echocano.ai.news.infrastructure.adapter.output.newsapi.client;

import com.echocano.ai.news.application.exceptions.ApiNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiRequest;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.NewsApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.test.autoconfigure.AutoConfigureCache;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@AutoConfigureCache
@RestClientTest(NewsApiOutputAdapter.class)
@ActiveProfiles("test")
class NewsApiOutputAdapterTest {

    @Value("${api.news-api.key}")
    private String apiKey;

    @Value("${api.news-api.url.base}")
    private String baseUrl;

    @Value("${api.news-api.url.content}")
    private String headlines;

    @Autowired
    private NewsApiOutputAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    @Test
    @DisplayName("Should return News Successfully")
    void test1() {
        String country = "US";
        String expectedUrl = baseUrl + headlines + "?country=" + country + "&apiKey=" + apiKey;
        NewsApiRequest request = NewsApiRequest.builder().country(country).build();
        String responseJson = """
                {
                    "totalResults": 1,
                    "articles": [
                        {
                            "source": {
                                "id": "the-times-of-india",
                                "name": "The Times of India"
                            },
                            "author": "NYT News Service",
                            "title": "China's edge in an oil shock: How EVs and renewables cushion the blow",
                            "description": "China enjoys a distinct edge as oil prices surge. Decades of investment in electric vehicles and renewable energy sources are now paying off. This strategy reduces reliance on foreign oil, making China less vulnerable to global energy market disruptions. Many…",
                            "url": "https://economictimes.indiatimes.com/news/international/business/chinas-edge-in-an-oil-shock-how-evs-and-renewables-cushion-the-blow/articleshow/129568696.cms",
                            "urlToImage": "https://img.etimg.com/thumb/msid-129568788,width-1200,height-630,imgsize-51836,overlay-economictimes/articleshow.jpg",
                            "publishedAt": "2026-03-14T04:40:01Z",
                            "content": "Hong Kong: As the price of oil soars to $100 a barrel and countries scramble to limit the fallout of the sudden loss of Middle East fuel, China has two significant advantages over its geopolitical ri… [+6533 chars]"
                        }
                    ]
                }""";

        this.server.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));
        NewsApiResponse response = adapter.getNews(request);

        assertNotNull(response);
        assertEquals(1, response.getTotalResults());
        this.server.verify();
    }

    @Test
    @DisplayName("Should throw a ApiNotAvailableException")
    void test2() {
        String country = "US";
        NewsApiRequest baseRequest = NewsApiRequest.builder().country(country).build();
        String expectedUrl = baseUrl + headlines + "?country=" + country + "&apiKey=" + apiKey;
        this.server.expect(requestTo(expectedUrl))
                .andRespond(request -> {
                    throw new ResourceAccessException(
                            "Connection refused",
                            new IOException("Socket closed")
                    );
                });
        assertThrows(ApiNotAvailableException.class, () -> adapter.getNews(baseRequest));
        this.server.verify();
    }

    @Test
    @DisplayName("Should throw a NotDefineException")
    void test3() {
        String country = "US";
        NewsApiRequest baseRequest = NewsApiRequest.builder().country(country).build();
        String expectedUrl = baseUrl + headlines + "?country=" + country + "&apiKey=" + apiKey;
        this.server.expect(requestTo(expectedUrl))
                .andRespond(withServerError()); // Simulates a 500 error
        assertThrows(NotDefineException.class, () -> adapter.getNews(baseRequest));
        this.server.verify();
    }
}