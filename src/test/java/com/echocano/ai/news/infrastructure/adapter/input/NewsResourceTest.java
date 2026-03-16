package com.echocano.ai.news.infrastructure.adapter.input;

import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import com.echocano.ai.news.infrastructure.port.input.GetNewsSummaryInputPort;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.cache.test.autoconfigure.AutoConfigureCache;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureCache
@AutoConfigureMetrics
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureJson
@Slf4j
class NewsResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetNewsSummaryInputPort summaryInputPort;

    @Test
    @DisplayName("Should return a Summary")
    void test1() throws Exception {
        // GIVEN
        String country = "us";
        NewsSummaryResponse mockResponse = NewsSummaryResponse.builder()
                .summary("Test Summary")
                .countryCode("us")
                .date(LocalDate.of(2026, 3, 15))
                .build();

        given(summaryInputPort.getSummary(any(NewsSummaryRequest.class)))
                .willReturn(mockResponse);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/news")
                        .queryParam("country", country)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("Test Summary"))
                .andExpect(jsonPath("$.countryCode").value(country));
    }
}