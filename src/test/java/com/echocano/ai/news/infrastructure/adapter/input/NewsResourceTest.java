package com.echocano.ai.news.infrastructure.adapter.input;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import com.echocano.ai.news.infrastructure.port.input.GetNewsSummaryInputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.cache.test.autoconfigure.AutoConfigureCache;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

@AutoConfigureCache
@WebMvcTest(NewsResource.class)
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
                .date(LocalDate.now())
                .build();

        given(summaryInputPort.getSummary(any(NewsSummaryRequest.class)))
                .willReturn(mockResponse);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/news?country=", country)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("Test Summary"))
                .andExpect(jsonPath("$.countryCode").value(country));
    }
}