package com.example.stockmarketbot.util;

import com.example.stockmarketbot.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
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
public class KeyboardService {
    @Autowired
    private ApplicationProperties applicationProperties;
    public synchronized void setButtonsToMainMenu(SendMessage sendMessage) {
        //создаём список из кнопок
        /*
        // вынести ?
         */
        List<KeyboardButton> buttons = new ArrayList<>(){{
            add(new KeyboardButton("/start"));
            add(new KeyboardButton("/help"));
            add(new KeyboardButton("/lang"));
            add(new KeyboardButton("/getBalanceByCurrency"));
            add(new KeyboardButton("/getTransactionsByFilter"));
        }};
        // Создаем клавиатуру
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(keyboard);
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow(); // создаём строку, на каждой строке будет по две кнопки это указано в условии цикла


        int counterAddedButtons = 0; // счётчик кнопок на строке

        for(int i = 0; i < buttons.size() ; i++) {
            if(counterAddedButtons == applicationProperties.getNumberOfButtonsInRowReplyKeyboardsMarkup()) { // Если уже добавлено три кнопки на одну строку, то создаём новую строку
                keyboardRows.add(row);
                counterAddedButtons = 0;
                row = new KeyboardRow();
            }
            row.add(buttons.get(i));
            counterAddedButtons++;
            if( i == buttons.size() - 1) {
                keyboardRows.add(row);
            }
        }

        // и устанавливаем этот список нашей клавиатуре
        keyboard.setKeyboard(keyboardRows);
    }

    public SendMessage setKeyboardToMessage(Long chatId, String text, List<String> buttons) {
        SendMessage message = getMessage(chatId, text);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(); // создали объект клавиатуры

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>(); // Создали список для строк которые будут прикреплены к сообщению

        List<InlineKeyboardButton> row = new ArrayList<>(); // строка

        int counterAddedButtons = 0; // сколько кнопок будет на строке

        for(int i = 0; i < buttons.size() ; i++) {
            if(counterAddedButtons == applicationProperties.getNumberOfButtonsInRowInlineKeyboards()) { // Если уже добавлено три кнопки на одну строку, то создаём новую строку
                keyboardRows.add(row);
                counterAddedButtons = 0;
                row = new ArrayList<>() ;
            }
            String buttonText = buttons.get(i);
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonText);
            button.setCallbackData(buttonText);
            row.add(button);
            counterAddedButtons++;
            if( i == buttons.size() - 1) {
                keyboardRows.add(row);
            }
        }

        keyboardMarkup.setKeyboard(keyboardRows); //Добавляем строки в клавиатуру
        message.setReplyMarkup(keyboardMarkup);// Добавляем клавиатуру к сообщению

        return message;
    }

    /*
     в stockMarketBot уже есть похожий метод
     */
    private SendMessage getMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        return sendMessage;
    }
}
