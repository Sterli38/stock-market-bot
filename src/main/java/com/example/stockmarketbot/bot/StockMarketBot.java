package com.example.stockmarketbot.bot;

import com.example.stockmarketbot.response.StockMarketResponseGetTransactionsByFilter;
import com.example.stockmarketbot.service.StockMarketService;
import com.example.stockmarketbot.util.TransactionFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class StockMarketBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String DOCUMENT = "/document";

    @Autowired
    private StockMarketService stockMarketService;

    public StockMarketBot(@Value("${stock.market.bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {

            String message = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            String username = update.getMessage().getChat().getUserName();

            switch (message) {
                case START -> startCommand(chatId, username);
                case HELP -> helpCommand(chatId);
                case DOCUMENT -> {
                    TransactionFilter transactionFilter = new TransactionFilter();
                    transactionFilter.setOperationType("DEPOSITING");
                    sendDocument(chatId, "Транзакции за что ?", getDoc(stockMarketService.getTransactionsByFilter("1", "egor", "egor", transactionFilter)));
                }
                default -> unknownCommand(chatId);
            }
        }
    }

    public void startCommand(Long chatId, String userName) {
        String text = "Добро пожаловать в stockMarketBot, %s!" +
                "\nДоступные команды:" +
                "\n/help - получение справки";
        String format = String.format(text, userName);
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

    private SendMessage sendMessage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
        return sendMessage;
    }

    private void sendDocument(Long chatId, String caption, InputFile document) {
        SendDocument document1 = new SendDocument();
        document1.setChatId(chatId);
        document1.setCaption(caption);
        document1.setDocument(document);

        try {
            execute(document1);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private InputFile getDoc(List<StockMarketResponseGetTransactionsByFilter> response) {
        File profileFile = null;

        try {
            profileFile = ResourceUtils.getFile("src/main/resources/static/participantTransactions");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());

             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new InputFile(profileFile);
    }

    @Override
    public String getBotUsername() {
        return "stockMarketBot";
    }
}
