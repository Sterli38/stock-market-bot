package com.example.stockmarketbot.util;

import com.example.stockmarketbot.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeyboardService {
    private final ApplicationProperties applicationProperties;
    private final List<String> mainMenuButtons = new ArrayList<>() {{
        add("/start");
        add("/help");
        add("/lang");
        add("/getBalanceByCurrency");
        add("/getTransactionsByFilter");
    }};

    public synchronized void setButtonsToMainMenu(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(keyboard);
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < mainMenuButtons.size(); i++) {
            if (row.size() == applicationProperties.getNumberOfButtonsInRowReplyKeyboardsMarkup()) {
                keyboardRows.add(row);
                row = new KeyboardRow();
            }
            row.add(new KeyboardButton(mainMenuButtons.get(i)));
            if (i == mainMenuButtons.size() - 1) {
                keyboardRows.add(row);
            }
        }

        keyboard.setKeyboard(keyboardRows);
    }

    public void setKeyboardToMessage(SendMessage sendMessage, List<String> buttons) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(keyboard);
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (int i = 0; i < buttons.size(); i++) {
            if (row.size() == applicationProperties.getNumberOfButtonsInRowInlineKeyboards()) {
                keyboardRows.add(row);
                row = new ArrayList<>();
            }
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(buttons.get(i));
            inlineKeyboardButton.setCallbackData(buttons.get(i));
            row.add(inlineKeyboardButton);
            if (i == buttons.size() - 1) {
                keyboardRows.add(row);
            }
        }

        keyboard.setKeyboard(keyboardRows);
    }
}
