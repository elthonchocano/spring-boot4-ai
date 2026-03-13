package com.echocano.ai.news.infrastructure.adapter.output.ollama.client;

import com.echocano.ai.news.application.exceptions.ApiNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.test.autoconfigure.AutoConfigureCache;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@AutoConfigureCache
@RestClientTest(SummaryOutputAdapter.class)
@ActiveProfiles("test")
class SummaryOutputAdapterTest {

    @Value("${api.ollama.url.base}")
    private String baseUrl;

    @Value("${api.ollama.url.generate}")
    private String generatePath;

    @Value("${api.ollama.url.body.model}")
    private String modelName;

    @Autowired
    private SummaryOutputAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    private static final String JSON_NULL_STRING = "";

    @Test
    @DisplayName("Should return Summary Successfully")
    void test1() {
        String responseJson = "{\"response\" : \"Success\"}";

        this.server.expect(requestTo(baseUrl + generatePath))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(jsonPath("$.model").value(modelName))
                .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        String result = adapter.getSummary("Test prompt");

        assertEquals("Success", result);
        this.server.verify();
    }

    @Test
    @DisplayName("Should throw a ResourceAccessException")
    void test2() {
        this.server.expect(requestTo(baseUrl + generatePath))
                .andRespond(request -> {
                    throw new ResourceAccessException(
                            "Connection refused",
                            new IOException("Socket closed")
                    );
                });

        assertThrows(ApiNotAvailableException.class, () -> adapter.getSummary("Test prompt"));
    }

    @Test
    @DisplayName("Should throw a NotDefineException")
    void test3() {
        this.server.expect(requestTo(baseUrl + generatePath))
                .andRespond(withServerError()); // Simulates a 500 error
        assertThrows(NotDefineException.class, () -> adapter.getSummary("Test prompt"));
    }

    @Test
    @DisplayName("Should throw a ApiNotAvailableException")
    void test4() {
        this.server.expect(requestTo(baseUrl + generatePath))
                .andRespond(request -> {
                    throw new ApiNotAvailableException("Null respond");
                });
        assertThrows(ApiNotAvailableException.class, () -> adapter.getSummary("Test prompt"));
    }

    @Test
    @DisplayName("Should handle explicit null response from Ollama API")
    void test5() {
        this.server.expect(requestTo(baseUrl + generatePath))
                .andRespond(withSuccess(JSON_NULL_STRING, MediaType.APPLICATION_JSON));

        assertThrows(ApiNotAvailableException.class, () -> adapter.getSummary("Test prompt"));
        this.server.verify();
    }
}