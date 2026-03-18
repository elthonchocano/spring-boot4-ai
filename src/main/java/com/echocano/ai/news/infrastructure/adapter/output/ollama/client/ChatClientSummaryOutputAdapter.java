package com.echocano.ai.news.infrastructure.adapter.output.ollama.client;

import com.echocano.ai.news.application.exceptions.NotDefineException;
import com.echocano.ai.news.application.exceptions.ServiceNotAvailableException;
import com.echocano.ai.news.application.port.output.SummaryOutputPort;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service("ollamaAdapter")
@ConditionalOnProperty(name = "spring.ai.provider", havingValue = "chatClient")
public class ChatClientSummaryOutputAdapter implements SummaryOutputPort {

    private final ChatClient chatClient;
    private final ObservationRegistry observationRegistry;

    public ChatClientSummaryOutputAdapter(ChatClient.Builder builder, ObservationRegistry observationRegistry) {
        this.chatClient = builder.build();
        this.observationRegistry = observationRegistry;
    }

    @Retryable(
            includes = TransientAiException.class,
            maxRetries = 3,
            delay = 2000
    )
    @ConcurrencyLimit(value = 10)
    @Override
    public String getSummary(String content) {
        String summary;
        log.info("Using chatClient");
        try {
            Observation observation = Observation.createNotStarted("ai.summarization", observationRegistry);
            observation.contextualName("ollama-chatClient-summary-task");
            observation.lowCardinalityKeyValue("ai.provider", "ollama");
            summary = observation.observe(() ->
                    this.chatClient.prompt()
                            .user(content)
                            .call()
                            .content()
            );
        } catch (TransientAiException e) {
            throw new ServiceNotAvailableException("The summary service is temporarily unavailable. Please try again.");
        } catch (NonTransientAiException e) {
            throw new ServiceNotAvailableException("Internal error: Could not generate summary.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NotDefineException(e.getMessage());
        }
        return summary;
    }
}
