package com.example.stockmarketbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class StockMarketBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";
    public StockMarketBot(@Value("${stock.market.bot.token}")String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        switch (message) {
            case START -> {
                String username = update.getMessage().getChat().getUserName();
                startCommand(chatId, username);
            }
            case HELP ->
                    helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }

    public void startCommand(Long chatId, String userName) {
        String text = "Добро пожаловать в stockMarketBot, %s!" +
                "\nДоступные команды:" +
                "\n/help - получение справки";
        String format= String.format(text, userName);
        sendMessage(chatId, format);
    }

    public void helpCommand(Long chatId) {
        String text = "Доступные команды:" +
                "\n/start - запуск начального меню" +
                "\n/help - просмотр доступных команд";
        sendMessage(chatId, text);
    }

    public void unknownCommand(Long chatId) {
        String text = "Неопознанная команда, для просмотра доступных команд воспользуйтесь: /help";
        sendMessage(chatId, text);
    }

    public void sendMessage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "stockMarketBot";
    }
}
