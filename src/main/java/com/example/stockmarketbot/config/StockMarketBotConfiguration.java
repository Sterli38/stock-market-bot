package com.example.stockmarketbot.config;

import com.example.stockmarketbot.bot.StockMarketBot;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class StockMarketBotConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(StockMarketBot stockMarketBot) throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class) {{
            registerBot(stockMarketBot);
        }};
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
