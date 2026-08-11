package com.skillpilot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini")
@Getter
@Setter
public class GeminiProperties {

    private String apiKey;
    private String model = "gemini-flash-latest";
    private boolean enabled = true;
    private int timeoutMs = 15000;
    private double temperature = 0.1;
    private int maxOutputTokens = 1000;
}
