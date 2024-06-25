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
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.stockmarketbot.bot.StockMarketBot.EUR;
import static com.example.stockmarketbot.bot.StockMarketBot.RUB;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
    private final StockMarketService stockMarketService;
    private final KeyboardService keyboardService;
    private final LanguageSettings languageSettings;

    public SendMessage handleStartCommand(Long chatId, String userName) {
        String text = languageSettings.getLocalizedMessage("start.message", new Object[]{userName});

        return getMessage(chatId, text);
    }

    public SendMessage handleHelpCommand(Long chatId) {
        String text = languageSettings.getLocalizedMessage("help.message", null);

        return getMessage(chatId, text);
    }

    public SendMessage handleLangCommand(Long chatId) {
        String text = languageSettings.getLocalizedMessage("lang.message", null);
        List<String> buttons = new ArrayList<>() {{
            add("EN");
            add("RU");
        }};

        SendMessage sendMessage = getMessage(chatId, text);

        keyboardService.setKeyboardToMessage(sendMessage, buttons);

        return sendMessage;
    }

    public SendMessage handleRuCommand(Long chatId) {
        languageSettings.setLocale(new Locale("ru", "ru"));
        return getMessage(chatId, languageSettings.getLocalizedMessage("change.language.message", null));
    }

    public SendMessage handleEnCommand(Long chatId) {
        languageSettings.setLocale(Locale.US);
        return getMessage(chatId, languageSettings.getLocalizedMessage("change.language.message", null));
    }

    public SendMessage handleGetBalanceByCurrencyCommand(Long chatId) {
        String text = languageSettings.getLocalizedMessage("getBalance.message", null);

        List<String> buttons = new ArrayList<>(){{
            add("EUR");
            add("RUB");
        }};

        SendMessage sendMessage = getMessage(chatId, text);
        keyboardService.setKeyboardToMessage(sendMessage, buttons);

        return sendMessage;
    }

    public SendMessage handleEURCommand(Long chatId) {
        GetBalanceByCurrencyRequest getBalanceByCurrencyRequest = new GetBalanceByCurrencyRequest();
        getBalanceByCurrencyRequest.setParticipantId("1");
        getBalanceByCurrencyRequest.setCurrency("EUR");
        GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = stockMarketService.getBalanceByCurrency("egor", "egor", getBalanceByCurrencyRequest);

        return getMessage(chatId, languageSettings.getLocalizedMessage("getBalance.response.message", new Object[]{EUR}) + getBalanceByCurrencyResponse.getCurrencyBalance());
    }

    public SendMessage handleRUBCommand(Long chatId) {
        GetBalanceByCurrencyRequest getBalanceByCurrencyRequest = new GetBalanceByCurrencyRequest();
        getBalanceByCurrencyRequest.setParticipantId("1");
        getBalanceByCurrencyRequest.setCurrency("RUB");
        GetBalanceByCurrencyResponse getBalanceByCurrencyResponse = stockMarketService.getBalanceByCurrency("egor", "egor", getBalanceByCurrencyRequest);

       return getMessage(chatId, languageSettings.getLocalizedMessage("getBalance.response.message", new Object[]{RUB}) + getBalanceByCurrencyResponse.getCurrencyBalance());
    }

    public SendDocument handleGetTransactionsByFilterCommand(Long chatId) {
        GetTransactionsByFilterRequest getTransactionsByFilterRequest = new GetTransactionsByFilterRequest();
        getTransactionsByFilterRequest.setParticipantId("1");
        getTransactionsByFilterRequest.setOperationType("DEPOSITING");

        return getDoc(chatId, stockMarketService.getTransactionsByFilter("egor", "egor", getTransactionsByFilterRequest));
    }

    public SendMessage handleUnknownCommand(Long chatId) {
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
