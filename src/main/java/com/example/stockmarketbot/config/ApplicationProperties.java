package com.example.stockmarketbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ApplicationProperties {
    @Value("${stock.market.service.url}")
    private String stockMarketServiceUrl;
}
