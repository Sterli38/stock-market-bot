package com.example.stockmarketbot.bot;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.integration.stockmarket.request.GetBalanceByCurrencyRequest;
import com.example.stockmarketbot.integration.stockmarket.request.GetTransactionsByFilterRequest;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.integration.stockmarket.response.GetBalanceByCurrencyResponse;
import com.example.stockmarketbot.service.StockMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import java.util.Locale;

@Slf4j
@Component
public class StockMarketBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String GETTRANSACTIONSBYFILTER = "/getTransactionsByFilter";
    private static final String LANG = "/lang";
    private static final String GETBALANCEBYCURRENCY = "/getBalanceByCurrency";
    private static final String EUR = "EUR";
    private static final String RUB = "RUB"; // вынести
    private static final String EN = "EN";
    private static final String RU = "RU";
    private Locale local = Locale.US;
    @Autowired
    private StockMarketService stockMarketService;
    @Autowired
    private MessageSource messageSource;

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
                case LANG -> handleLangCommand(chatId);
                case GETTRANSACTIONSBYFILTER -> {
                    GetTransactionsByFilterRequest getTransactionsByFilterRequest = new GetTransactionsByFilterRequest();
                    getTransactionsByFilterRequest.setParticipantId("1");
                    getTransactionsByFilterRequest.setOperationType("DEPOSITING");
                    handleDocumentCommand(chatId, getTransactionsByFilterRequest);
                }
                case GETBALANCEBYCURRENCY -> getBalanceByCurrency(chatId);
                default -> handleUnknownCommand(chatId);
            }

        } else if (update.hasCallbackQuery()) { // Если пользователь нажал на кнопку ( передал id кнопки (CallBackData))
            String callData = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();

            GetBalanceByCurrencyRequest getBalanceByCurrencyRequest = new GetBalanceByCurrencyRequest();
            getBalanceByCurrencyRequest.setParticipantId("1");
            switch (callData) {
                case EUR -> {
                    getBalanceByCurrencyRequest.setCurrency("EUR");
                    GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = stockMarketService.getBalanceByCurrency("egor", "egor", getBalanceByCurrencyRequest); // когда нажимаю кнопку, повторно нажать её не могу без перезапуска, понять почему так
                    sendMessage(chatId, getLocalizedMessage("getBalance.response.message", new Object[]{EUR}) + getBalanceByCurrencyResponse.getCurrencyBalance());
                }
                case RUB -> {
                    getBalanceByCurrencyRequest.setCurrency("RUB");
                    GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = stockMarketService.getBalanceByCurrency("egor", "egor", getBalanceByCurrencyRequest);
                    sendMessage(chatId, getLocalizedMessage("getBalance.response.message", new Object[]{RUB}) + getBalanceByCurrencyResponse.getCurrencyBalance()); // когда нажимаю кнопку, повторно нажать её не могу без перезапуска, понять почему так
                }
                case EN -> {
                    local = Locale.US;
                    sendMessage(chatId, getLocalizedMessage("change.language.message", null)); // дублирование ?
                }
                case RU -> {
                    local = new Locale("ru", "ru");
                    sendMessage(chatId, getLocalizedMessage("change.language.message", null));
                }
            }
        }
    }

    public void handleLangCommand(Long chatId) {
        String text = messageSource.getMessage("lang.message", null, local);

        SendMessage message = new SendMessage(String.valueOf(chatId), text);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(); // создаём объект встроенной клавиатуры
        List<List<InlineKeyboardButton>> rowInLine = new ArrayList<>(); // создали список списков кнопок который объеденяет ряды кнопок
        List<InlineKeyboardButton> buttonList = new ArrayList<>(); // создаём список кнопок для первого ряда

        InlineKeyboardButton eurButton = new InlineKeyboardButton(); // создаём первую кнопку
        eurButton.setText("EN");
        eurButton.setCallbackData("EN"); // идентификатор, который позволяет боту понять какая кнопка была нажата

        InlineKeyboardButton rubButton = new InlineKeyboardButton();// создаём вторую кнопку
        rubButton.setText("RU");
        rubButton.setCallbackData("RU"); // идентификатор, который позволяет боту понять какая кнопка была нажата

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

    public void handleStartCommand(Long chatId, String userName) {
        String text = getLocalizedMessage("start.message", new Object[]{userName});

        sendMessage(chatId, text);
    }

    public void handleDocumentCommand(Long chatId, GetTransactionsByFilterRequest getTransactionsByFilterRequest) {
        sendDocument(
                chatId,
                "Транзакции за что ?",
                getDoc(stockMarketService.getTransactionsByFilter("egor", "egor", getTransactionsByFilterRequest))
        );
    }

    public void handleHelpCommand(Long chatId) {
        String text = getLocalizedMessage("help.message", null);

        sendMessage(chatId, text);
    }
    public void handleUnknownCommand(Long chatId) {
        String text = getLocalizedMessage("unknown.message", null);

        sendMessage(chatId, text);
    }

    public void getBalanceByCurrency(Long chatId) {
        String text = getLocalizedMessage("getBalance.message", null);

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

    private String getLocalizedMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, local);
    }

    @Override
    public String getBotUsername() {
        return "stockMarketBot";
    }
}
