package com.example.stockmarketbot.service;

import com.example.stockmarketbot.config.LanguageSettings;
import com.example.stockmarketbot.integration.stockmarket.request.GetBalanceByCurrencyRequest;
import com.example.stockmarketbot.integration.stockmarket.request.GetTransactionsByFilterRequest;
import com.example.stockmarketbot.integration.stockmarket.response.GetBalanceByCurrencyResponse;
import com.example.stockmarketbot.integration.stockmarket.response.GetTransactionsByFilterResponse;
import com.example.stockmarketbot.util.KeyboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String GET_TRANSACTIONS_BY_FILTER = "/getTransactionsByFilter";
    private static final String LANG = "/lang";
    private static final String GET_BALANCE_BY_CURRENCY = "/getBalanceByCurrency";
    private static final String EUR = "EUR";
    private static final String RUB = "RUB";
    private static final String EN = "EN";
    private static final String RU = "RU";
    private final StockMarketService stockMarketService;
    private final KeyboardService keyboardService;
    private final LanguageSettings languageSettings;

    public PartialBotApiMethod<Message> handle(Update update) {
        PartialBotApiMethod<Message> result = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String username = update.getMessage().getChat().getUserName();
            Long chatId = update.getMessage().getChatId();

            result = switch (message) {
                case START -> handleStartCommand(chatId, username);
                case HELP -> handleHelpCommand(chatId);
                case LANG -> handleLangCommand(chatId);
                case GET_TRANSACTIONS_BY_FILTER -> handleGetTransactionsByFilterCommand(chatId);
                case GET_BALANCE_BY_CURRENCY -> handleGetBalanceByCurrencyCommand(chatId);
                default -> handleUnknownCommand(chatId);
            };
        } else if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            result = switch (callData) {
                case EUR -> handleEURCommand(chatId);
                case RUB -> handleRUBCommand(chatId);
                case EN -> handleEnCommand(chatId);
                case RU -> handleRuCommand(chatId);
                default -> throw new IllegalStateException("Unexpected value: " + callData);
            };
        }
        return result;
    }

    private SendMessage handleStartCommand(Long chatId, String userName) {
        String text = languageSettings.getLocalizedMessage("start.message", new Object[]{userName});

        return getMessage(chatId, text);
    }

    private SendMessage handleHelpCommand(Long chatId) {
        String text = languageSettings.getLocalizedMessage("help.message", null);

        return getMessage(chatId, text);
    }

    private SendMessage handleLangCommand(Long chatId) {
        String text = languageSettings.getLocalizedMessage("lang.message", null);
        List<String> buttons = new ArrayList<>() {{
            add("EN");
            add("RU");
        }};

        SendMessage sendMessage = getMessage(chatId, text);

        keyboardService.setKeyboardToMessage(sendMessage, buttons);

        return sendMessage;
    }

    private SendMessage handleRuCommand(Long chatId) {
        languageSettings.setLocale(new Locale("ru", "ru"));
        return getMessage(chatId, languageSettings.getLocalizedMessage("change.language.message", null));
    }

    private SendMessage handleEnCommand(Long chatId) {
        languageSettings.setLocale(Locale.US);
        return getMessage(chatId, languageSettings.getLocalizedMessage("change.language.message", null));
    }

    private SendMessage handleGetBalanceByCurrencyCommand(Long chatId) {
        String text = languageSettings.getLocalizedMessage("getBalance.message", null);

        List<String> buttons = new ArrayList<>() {{
            add("EUR");
            add("RUB");
        }};

        SendMessage sendMessage = getMessage(chatId, text);
        keyboardService.setKeyboardToMessage(sendMessage, buttons);

        return sendMessage;
    }

    private SendMessage handleEURCommand(Long chatId) {
        GetBalanceByCurrencyRequest getBalanceByCurrencyRequest = new GetBalanceByCurrencyRequest();
        getBalanceByCurrencyRequest.setParticipantId("1");
        getBalanceByCurrencyRequest.setCurrency("EUR");
        GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = stockMarketService.getBalanceByCurrency("egor", "egor", getBalanceByCurrencyRequest);

        return getMessage(chatId, languageSettings.getLocalizedMessage("getBalance.response.message", new Object[]{EUR}) + getBalanceByCurrencyResponse.getCurrencyBalance());
    }

    private SendMessage handleRUBCommand(Long chatId) {
        GetBalanceByCurrencyRequest getBalanceByCurrencyRequest = new GetBalanceByCurrencyRequest();
        getBalanceByCurrencyRequest.setParticipantId("1");
        getBalanceByCurrencyRequest.setCurrency("RUB");
        GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = stockMarketService.getBalanceByCurrency("egor", "egor", getBalanceByCurrencyRequest);

        return getMessage(chatId, languageSettings.getLocalizedMessage("getBalance.response.message", new Object[]{RUB}) + getBalanceByCurrencyResponse.getCurrencyBalance());
    }

    private SendDocument handleGetTransactionsByFilterCommand(Long chatId) {
        GetTransactionsByFilterRequest getTransactionsByFilterRequest = new GetTransactionsByFilterRequest();
        getTransactionsByFilterRequest.setParticipantId("1");
        getTransactionsByFilterRequest.setOperationType("DEPOSITING");

        return getDoc(chatId, stockMarketService.getTransactionsByFilter("egor", "egor", getTransactionsByFilterRequest));
    }

    private SendMessage handleUnknownCommand(Long chatId) {
        String text = languageSettings.getLocalizedMessage("unknown.message", null);

        return getMessage(chatId, text);
    }

    private SendMessage getMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        keyboardService.setButtonsToMainMenu(sendMessage);

        return sendMessage;
    }

    private SendDocument getDoc(Long chatId, List<GetTransactionsByFilterResponse> response) {
        byte[] value = response.toString().getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value);

        return new SendDocument(String.valueOf(chatId), new InputFile(byteArrayInputStream, "ParticipantTransactions.txt"));
    }
}
