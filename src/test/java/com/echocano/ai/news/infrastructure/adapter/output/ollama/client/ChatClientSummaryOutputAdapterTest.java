package com.echocano.ai.news.infrastructure.adapter.output.ollama.client;

import com.echocano.ai.news.application.exceptions.ServiceNotAvailableException;
import com.echocano.ai.news.application.exceptions.NotDefineException;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatClientSummaryOutputAdapterTest {

    @Mock
    private ChatClient.Builder builder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private ChatClientSummaryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        when(builder.build()).thenReturn(chatClient);
        ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
        adapter = new ChatClientSummaryOutputAdapter(builder, observationRegistry);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("Should return Summary Successfully")
    void test1() {
        String inputContent = "Spring AI news summary test content.";
        String expectedSummary = "This is a summary.";

        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn(expectedSummary);

        String result = adapter.getSummary(inputContent);

        assertEquals(expectedSummary, result);

        verify(chatClient).prompt();
        verify(requestSpec).user(inputContent);
        verify(responseSpec).content();
    }

    @Test
    @DisplayName("Should throws a TransientAiException")
    void test2() {
        when(responseSpec.content()).thenThrow(new TransientAiException("Simulated Timeout"));
        assertThrows(ServiceNotAvailableException.class, () -> adapter.getSummary("content"));
    }

    @Test
    @DisplayName("Should throws a NonTransientAiException")
    void test3() {
        when(responseSpec.content()).thenThrow(new NonTransientAiException("Bad Model Name"));
        assertThrows(ServiceNotAvailableException.class, () -> adapter.getSummary("content"));
    }

    @Test
    @DisplayName("Should throws a NotDefineException")
    void test4() {
        when(responseSpec.content()).thenThrow(new RuntimeException("Something went wrong"));
        assertThrows(NotDefineException.class, () -> adapter.getSummary("content"));
    }
}