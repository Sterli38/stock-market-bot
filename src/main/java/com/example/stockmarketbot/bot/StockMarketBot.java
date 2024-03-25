package com.example.stockmarketbot.bot;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.integration.stockmarket.request.TransactionFilter;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.integration.stockmarket.response.StockMarketResponse;
import com.example.stockmarketbot.service.StockMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StockMarketBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String DOCUMENT = "/document";
    private static final String GETBALANCEBYCURRENCY = "/getBalanceByCurrency";
    private static final String EUR = "EUR";
    private static final String RUB = "RUB"; // вынести
    @Autowired
    private StockMarketService stockMarketService;

    public StockMarketBot(ApplicationProperties applicationProperties) {
        super(applicationProperties.getBotToken());
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {

            String message = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            String username = update.getMessage().getChat().getUserName();

            switch (message) {
                case START -> handleStartCommand(chatId, username);
                case HELP -> handleHelpCommand(chatId);
                case DOCUMENT -> {
                    TransactionFilter transactionFilter = new TransactionFilter();
                    transactionFilter.setOperationType("DEPOSITING");
                    sendDocument(chatId, "Транзакции за что ?", getDoc(stockMarketService.getTransactionsByFilter("1", "egor", "egor", transactionFilter))); // Тут нужно изменить подпись к файлу ( по какому фильтру были получены транзакции), так же нужно по другому передавать login, password и id ( возможно стоит сделать так чтобы у нас был метод авторизации, который будет по логину и паролю ходить в stockMarket и если человек уже зарегистрирован пропускать к возможностям бота, если нет то предлагать регистрацию
                }
                case GETBALANCEBYCURRENCY ->
                    getBalanceByCurrency(chatId);
                default -> handleUnknownCommand(chatId);
            }

        } else if(update.hasCallbackQuery()) { // Если пользователь нажал на кнопку ( передал id кнопки (CallBackData))
            String callData = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();

                switch (callData) {
                    case EUR -> {
                   // сделать аутентификацию чтобы id хранился локально ( то есть чтобы мы запрашивали его из кеша бота, предварительно получив после аутентификации)
                        StockMarketResponse stockMarketResponse = stockMarketService.getBalanceByCurrency("1", "EUR", "egor", "egor"); // когда нажимаю кнопку, повторно нажать её не могу без перезапуска, понять почему так
                        sendMessage(chatId, "Ваш баланс в EUR: " + stockMarketResponse.getCurrencyBalance());
                    }
                    case RUB -> {
                        StockMarketResponse stockMarketResponse = stockMarketService.getBalanceByCurrency("1", "RUB", "egor", "egor");
                        sendMessage(chatId, "Ваш баланс в RUB: " + stockMarketResponse.getCurrencyBalance()); // когда нажимаю кнопку, повторно нажать её не могу без перезапуска, понять почему так
                    }
                }
        }
    }

    public void handleStartCommand(Long chatId, String userName) {
        String text = "Добро пожаловать в stockMarketBot, %s!" +
                "\nДоступные команды:" +
                "\n/help - получение справки";
        String format = String.format(text, userName);
        sendMessage(chatId, format);
    }

    public void handleHelpCommand(Long chatId) {
        String text = "Доступные команды:" +
                "\n/start - запуск начального меню" +
                "\n/help - просмотр доступных команд";
        sendMessage(chatId, text);
    }

    public void handleUnknownCommand(Long chatId) {
        String text = "Неопознанная команда, для просмотра доступных команд воспользуйтесь: /help";
        sendMessage(chatId, text);
    }

    public void getBalanceByCurrency(Long chatId) {
        String text = "Введите валюту в которой хотите получить баланс";

        SendMessage message = new SendMessage(String.valueOf(chatId), text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); // создаём объект встроенной клавиатуры
        List<List<InlineKeyboardButton>> rowInLine = new ArrayList<>(); // создали список списков кнопок который объеденяет ряды кнопок
        List<InlineKeyboardButton> buttonList = new ArrayList<>(); // создаём список кнопок для первого ряда

        InlineKeyboardButton eurButton = new InlineKeyboardButton(); // создаём первую кнопку
        eurButton.setText("EUR");
        eurButton.setCallbackData("EUR"); // идентификатор, который позволяет боту понять какая кнопка была нажата

        InlineKeyboardButton rubButton = new InlineKeyboardButton();// создаём вторую кнопку
        rubButton.setText("RUB");
        rubButton.setCallbackData("RUB"); // идентификатор, который позволяет боту понять какая кнопка была нажата

        buttonList.add(eurButton);
        buttonList.add(rubButton);

        rowInLine.add(buttonList); // добавляем в первый ряд список кнопок

        inlineKeyboardMarkup.setKeyboard(rowInLine); // добавляем клавиатуру в сообщение
        message.setReplyMarkup(inlineKeyboardMarkup); //

        try {
            execute(message);
        } catch (TelegramApiException e) { // уже есть метод sendMessage !

        }

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

    private InputFile getDoc(List<GetTransactionsByFilterResponse> response) {
        File profileFile;

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
