package com.sku_sku.backend.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "gemini.api")
@Getter
@Setter
public class GeminiConfig {
    private String key;
    private String model;
    private String project;
    private String location;
    private Integer maxTokens;
    private Float temperature;
    private Integer topK;
    private Float topP;
}