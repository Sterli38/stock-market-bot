package com.example.stockmarketbot.bot;

import com.example.stockmarketbot.config.ApplicationProperties;
import com.example.stockmarketbot.integration.stockmarket.request.GetBalanceByCurrencyRequest;
import com.example.stockmarketbot.integration.stockmarket.request.GetTransactionsByFilterRequest;
import com.example.stockmarketbot.integration.stockmarket.response.GetBalanceByCurrencyResponse;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.service.StockMarketService;
import com.example.stockmarketbot.util.KeyboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class StockMarketBot extends TelegramLongPollingBot {
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String GET_TRANSACTIONS_BY_FILTER = "/getTransactionsByFilter";
    private static final String LANG = "/lang";
    private static final String GET_BALANCE_BY_CURRENCY = "/getBalanceByCurrency";
    private static final String EUR = "EUR";
    private static final String RUB = "RUB"; // вынести
    private static final String EN = "EN";
    private static final String RU = "RU";
    private Locale local = Locale.US;
    private final StockMarketService stockMarketService;
    private final MessageSource messageSource;
    private final KeyboardService keyboardService;

    public StockMarketBot(ApplicationProperties applicationProperties, StockMarketService stockMarketService, MessageSource messageSource, KeyboardService keyboardService) {
        super(applicationProperties.getBotToken());
        this.stockMarketService = stockMarketService;
        this.messageSource = messageSource;
        this.keyboardService = keyboardService;
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
                case GET_TRANSACTIONS_BY_FILTER -> {
                    GetTransactionsByFilterRequest getTransactionsByFilterRequest = new GetTransactionsByFilterRequest();
                    getTransactionsByFilterRequest.setParticipantId("1");
                    getTransactionsByFilterRequest.setOperationType("DEPOSITING");
                    handleDocumentCommand(chatId, getTransactionsByFilterRequest);
                }
                case GET_BALANCE_BY_CURRENCY -> handleGetBalanceByCurrencyCommand(chatId);
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
                    sendMessage(getMessage(chatId, getLocalizedMessage("getBalance.response.message", new Object[]{EUR}) + getBalanceByCurrencyResponse.getCurrencyBalance()));
                }
                case RUB -> {
                    getBalanceByCurrencyRequest.setCurrency("RUB");
                    GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = stockMarketService.getBalanceByCurrency("egor", "egor", getBalanceByCurrencyRequest);
                    sendMessage(getMessage(chatId, getLocalizedMessage("getBalance.response.message", new Object[]{RUB}) + getBalanceByCurrencyResponse.getCurrencyBalance())); // когда нажимаю кнопку, повторно нажать её не могу без перезапуска, понять почему так
                }
                case EN -> {
                    local = Locale.US;
                    sendMessage(getMessage(chatId, getLocalizedMessage("change.language.message", null))); // дублирование ?
                }
                case RU -> {
                    local = new Locale("ru", "ru");
                    sendMessage(getMessage(chatId, getLocalizedMessage("change.language.message", null)));
                }
            }
        }
    }

    public void handleLangCommand(Long chatId) {
        String text = messageSource.getMessage("lang.message", null, local);

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("EN");
        button.setCallbackData("EN");

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("RU");
        button1.setCallbackData("RU");

        List<InlineKeyboardButton> buttons = new ArrayList<>() {{
            add(button);
            add(button1);
        }};

        SendMessage sendMessage = keyboardService.setKeyboardToMessage(chatId, text, buttons);

        sendMessage(sendMessage);
    }

    public void handleStartCommand(Long chatId, String userName) {
        String text = getLocalizedMessage("start.message", new Object[]{userName});

        sendMessage(getMessage(chatId, text));
    }

    public void handleDocumentCommand(Long chatId, GetTransactionsByFilterRequest getTransactionsByFilterRequest) {
        sendDocument(getDoc(chatId, stockMarketService.getTransactionsByFilter("egor", "egor", getTransactionsByFilterRequest)));
    }

    public void handleHelpCommand(Long chatId) {
        String text = getLocalizedMessage("help.message", null);

        sendMessage(getMessage(chatId, text));
    }

    public void handleUnknownCommand(Long chatId) {
        String text = getLocalizedMessage("unknown.message", null);

        sendMessage(getMessage(chatId, text));
    }

    public void handleGetBalanceByCurrencyCommand(Long chatId) {
        String text = getLocalizedMessage("getBalance.message", null);

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("EUR");
        button.setCallbackData("EUR");

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("RUB");
        button1.setCallbackData("RUB");

        List<InlineKeyboardButton> buttons = new ArrayList<>(){{
            add(button);
            add(button1);
        }};

        SendMessage sendMessage = keyboardService.setKeyboardToMessage(chatId, text, buttons);

        sendMessage(sendMessage);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private SendMessage getMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        keyboardService.setButtonsToMainMenu(sendMessage);
        return sendMessage;
    }

    private void sendDocument(SendDocument document) {
        try {
            execute(document);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private SendDocument getDoc(Long chatId, List<GetTransactionsByFilterResponse> response) {
        byte[] value = response.toString().getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value);

        return new SendDocument(String.valueOf(chatId), new InputFile(byteArrayInputStream, "ParticipantTransactions.txt"));
    }

    private String getLocalizedMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, local);
    }

    @Override
    public String getBotUsername() {
        return "stockMarketBot";
    }
}
