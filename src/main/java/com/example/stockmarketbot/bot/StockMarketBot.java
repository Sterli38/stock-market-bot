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
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String GET_TRANSACTIONS_BY_FILTER = "/getTransactionsByFilter";
    private static final String LANG = "/lang";
    private static final String GET_BALANCE_BY_CURRENCY = "/getBalanceByCurrency";
    public static final String EUR = "EUR";
    public static final String RUB = "RUB"; // вынести
    private static final String EN = "EN";
    private static final String RU = "RU";
    private final CommandHandler commandHandler;
    public StockMarketBot(ApplicationProperties applicationProperties, CommandHandler commandHandler) {
        super(applicationProperties.getBotToken());
        this.commandHandler = commandHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {

            String message = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            String username = update.getMessage().getChat().getUserName();

            switch (message) {
                case START -> sendMessage(commandHandler.handleStartCommand(chatId, username));
                case HELP -> sendMessage(commandHandler.handleHelpCommand(chatId));
                case LANG -> sendMessage(commandHandler.handleLangCommand(chatId));
                case GET_TRANSACTIONS_BY_FILTER -> sendDocument(commandHandler.handleGetTransactionsByFilterCommand(chatId));
                case GET_BALANCE_BY_CURRENCY -> sendMessage(commandHandler.handleGetBalanceByCurrencyCommand(chatId));
                default -> sendMessage(commandHandler.handleUnknownCommand(chatId));
            }

        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (callData) {
                case EUR -> sendMessage(commandHandler.handleEURCommand(chatId));
                case RUB -> sendMessage(commandHandler.handleRUBCommand(chatId));
                case EN -> sendMessage(commandHandler.handleEnCommand(chatId));
                case RU -> sendMessage(commandHandler.handleRuCommand(chatId));
            }
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
