package org.example.controler;
import org.example.controler.dto.ButtonData;
import org.example.controler.dto.KeyboardMarkup;

import java.util.List;

/**
 * класс отвечающий за создание клавиатур
 */
public class KeyboardFactory {
    /**
     * Создает клавиатуру для выбора игры
     */
    public KeyboardMarkup createKeyboard(List<List<ButtonData>> buttonRows) {
        KeyboardMarkup keyboard = new KeyboardMarkup();
        keyboard.setKeyboardMarkup(buttonRows);
        return keyboard;
    }

}