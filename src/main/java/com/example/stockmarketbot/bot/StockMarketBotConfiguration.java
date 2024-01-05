package com.example.stockmarketbot.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class StockMarketBotConfiguration {
    @Bean
    public TelegramBotsApi telegramBotsApi(StockMarketBot stockMarketBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(stockMarketBot);
        return api;
    }
}

