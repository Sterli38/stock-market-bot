package com.example.stockmarketbot.bot;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.service.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class StockMarketBot extends TelegramLongPollingBot {
    private final CommandHandler commandHandler;

    public StockMarketBot(ApplicationProperties applicationProperties, CommandHandler commandHandler) {
        super(applicationProperties.getBotToken());
        this.commandHandler = commandHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(commandHandler.handle(update) instanceof SendMessage ) {
            sendMessage( (SendMessage) commandHandler.handle(update));
        } else if (commandHandler.handle(update) instanceof SendDocument){
            sendDocument( (SendDocument) commandHandler.handle(update));
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void sendDocument(SendDocument document) {
        try {
            execute(document);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "stockMarketBot";
    }
}
