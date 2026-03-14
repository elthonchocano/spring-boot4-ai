package com.echocano.ai.news.infrastructure.adapter.input.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.EnableResilientMethods;

@Configuration
@EnableResilientMethods // This replaces @EnableRetry
public class ResilienceConfig {
}
