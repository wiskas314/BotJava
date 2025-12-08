package org.example.controler;


import org.example.controler.dto.ButtonData;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * класс отвечающий за создание клавиатур
 */
public class KeyboardFactory {
    /**
     * Создает клавиатуру для выбора игры
     */
    public InlineKeyboardMarkup createKeyboard(List<List<ButtonData>> buttonRows) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (List<ButtonData> row : buttonRows){
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            for (ButtonData button : row){
                InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                keyboardButton.setText(button.getText());
                keyboardButton.setCallbackData(button.getCallbackData());
                keyboardRow.add(keyboardButton);
            }

            if(!keyboardRow.isEmpty()){
                rows.add(keyboardRow);
            }
        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }
    /**
     * Создает клавиатуру для выбора игры
     */
    public InlineKeyboardMarkup createGameSelectionKeyboard() {
        List<List<ButtonData>> buttonRows = new ArrayList<>();

        List<ButtonData> gameRow = new ArrayList<>();
        gameRow.add(new ButtonData("🎮 Ride the Bus", "ride_the_bus"));

        buttonRows.add(gameRow);
        return createKeyboard(buttonRows);
    }
}