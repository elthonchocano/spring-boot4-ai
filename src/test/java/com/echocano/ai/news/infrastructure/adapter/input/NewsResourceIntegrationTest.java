package com.echocano.ai.news.infrastructure.adapter.input;

import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import com.echocano.ai.news.infrastructure.port.input.GetNewsSummaryInputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableCaching
class NewsResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GetNewsSummaryInputPort summaryInputPort;

    @Test
    @DisplayName("Should retrieve from cache")
    void test1() throws Exception {
        given(summaryInputPort.getSummary(any())).willReturn(new NewsSummaryResponse());

        mockMvc.perform(get("/api/v1/news?country=ca"));

        mockMvc.perform(get("/api/v1/news?country=ca"));

        verify(summaryInputPort, times(1)).getSummary(any());
    }
}