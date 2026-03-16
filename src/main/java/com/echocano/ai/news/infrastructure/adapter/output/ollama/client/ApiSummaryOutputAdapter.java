package com.echocano.ai.news.infrastructure.adapter.output.ollama.client;

import com.echocano.ai.news.application.exceptions.NotDefineException;
import com.echocano.ai.news.application.exceptions.ServiceNotAvailableException;
import com.echocano.ai.news.infrastructure.adapter.output.ollama.dto.OllamaRequest;
import com.echocano.ai.news.infrastructure.adapter.output.ollama.dto.OllamaResponse;
import com.echocano.ai.news.infrastructure.port.output.SummaryOutputPort;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service("apiAdapter")
@ConditionalOnProperty(name = "spring.ai.provider", havingValue = "apiClient", matchIfMissing = true)
public class ApiSummaryOutputAdapter implements SummaryOutputPort {

    private final RestClient restClient;
    private final String notAvailableMsg;
    private final ObservationRegistry observationRegistry;
    @Value("${api.ollama.url.generate}")
    private String generate;
    @Value("${api.ollama.url.body.model}")
    private String model;

    public ApiSummaryOutputAdapter(RestClient.Builder builder
            , @Value("${api.ollama.url.base}") String baseUrl
            , ObservationRegistry observationRegistry) {
        this.restClient = builder.baseUrl(baseUrl).build();
        notAvailableMsg = String.format(
                "AI service %s is not available at this moment", baseUrl);
        this.observationRegistry = observationRegistry;
    }

    @Override
    @ConcurrencyLimit(value = 10)
    public String getSummary(String content) {
        String summary = null;
        try {
            OllamaResponse response = Observation.createNotStarted("ai.summarization", observationRegistry)
                    .lowCardinalityKeyValue("ai.model", "ollama-api")
                    .observe(() ->
                            restClient.post().uri(generate)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(OllamaRequest.builder().model(model).prompt(content).stream(false).build())
                                    .retrieve()
                                    .body(OllamaResponse.class)
            );
            if (response == null) {
                throw new ServiceNotAvailableException(notAvailableMsg);
            }
            summary = response.getResponse();
        } catch (ServiceNotAvailableException e) {
            throw e;
        } catch (ResourceAccessException e) {
            throw new ServiceNotAvailableException(notAvailableMsg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotDefineException(e.getMessage());
        }
        return summary;
    }
}
