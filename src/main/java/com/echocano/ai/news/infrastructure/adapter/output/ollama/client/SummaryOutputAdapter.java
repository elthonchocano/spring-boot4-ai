package com.echocano.ai.news.infrastructure.adapter.output.ollama.client;

import com.echocano.ai.news.application.exceptions.ApiNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import com.echocano.ai.news.infrastructure.adapter.output.ollama.dto.OllamaRequest;
import com.echocano.ai.news.infrastructure.adapter.output.ollama.dto.OllamaResponse;
import com.echocano.ai.news.infrastructure.port.output.SummaryOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class SummaryOutputAdapter implements SummaryOutputPort {

    @Value("${api.ollama.url.base}")
    private String baseUrl;

    @Value("${api.ollama.url.generate}")
    private String generate;

    @Value("${api.ollama.url.body.model}")
    private String model;

    @Override
    public String getSummary(String content) {
        String summary = null;
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        try {
            OllamaResponse response = restClient.post().uri(generate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(OllamaRequest.builder().model(model).prompt(content).stream(false).build())
                    .retrieve()
                    .body(OllamaResponse.class);
            if (response != null) {
                summary = response.getResponse();
            }
        } catch (ResourceAccessException e) {
            throw new ApiNotAvailableException(String.format(
                    "AI service %s is not available at this moment", baseUrl));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotDefineException(e.getMessage());
        }
        return summary;
    }
}
