package com.example.stockmarketbot.util;

import com.example.stockmarketbot.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class KeyboardService {
    private final ApplicationProperties applicationProperties;
    private static final List<String> mainMenuButtons = List.of(
            "/start",
            "/help",
            "/lang",
            "/getBalanceByCurrency",
            "/getTransactionsByFilter"
    );

    public synchronized void setButtonsToMainMenu(SendMessage sendMessage) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<List> keyboardRows = new ArrayList<>();
        List row = new KeyboardRow();

        fill(mainMenuButtons, row, applicationProperties.getNumberOfButtonsInRowReplyKeyboardsMarkup(), keyboardRows, (mainMenuButtons, index) -> new KeyboardButton(mainMenuButtons.get(index)));

        List<KeyboardRow> keyboardRows1 = keyboardRows.stream().map(KeyboardRow::new).toList();

        keyboard.setKeyboard(keyboardRows1);
        commons(sendMessage, keyboard);
    }

    public void setKeyboardToMessage(SendMessage sendMessage, List<String> buttons) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List> keyboardRows = new ArrayList<>();
        List row = new ArrayList<>();

        fill(buttons, row, applicationProperties.getNumberOfButtonsInRowInlineKeyboards(), keyboardRows, (i, index) -> new InlineKeyboardButton(buttons.get(index)));

        List<List<InlineKeyboardButton>> keyboardRows1 = keyboardRows.stream().map(i -> (List<InlineKeyboardButton>) i).toList();
        keyboard.setKeyboard(keyboardRows1);

        commons(sendMessage, keyboard);
    }

    private void fill(List<String> buttons, List<Object> row, int buttonsInRow, List<List> keyboardRows, BiFunction<List<String>, Integer, BotApiObject> bifunction) {
        for (int i = 0; i < buttons.size(); i++) {
            if (row.size() == buttonsInRow) {
                keyboardRows.add(row);
                row = new ArrayList<>();
            }

            row.add(bifunction.apply(buttons, i));

            if (i == buttons.size() - 1) {
                keyboardRows.add(row);
            }
        }
    }

    private void commons(SendMessage sendMessage, ReplyKeyboard keyboard) {
        sendMessage.setReplyMarkup(keyboard);
    }
}
