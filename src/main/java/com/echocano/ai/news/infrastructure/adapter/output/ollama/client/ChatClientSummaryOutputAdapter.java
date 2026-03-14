package com.echocano.ai.news.infrastructure.adapter.output.ollama.client;

import com.echocano.ai.news.application.exceptions.ServiceNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import com.echocano.ai.news.infrastructure.port.output.SummaryOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.resilience.annotation.Retryable;

@Slf4j
@Service("ollamaAdapter")
@Primary
public class ChatClientSummaryOutputAdapter implements SummaryOutputPort {

    private final ChatClient chatClient;

    public ChatClientSummaryOutputAdapter(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Retryable(
            includes = TransientAiException.class,
            maxRetries = 3,
            delay = 2000
    )
    @Override
    public String getSummary(String content) {
        String summary;
        log.info("Using chatClient");
        try {
            summary = this.chatClient.prompt()
                    .user(content)
                    .call()
                    .content();
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
