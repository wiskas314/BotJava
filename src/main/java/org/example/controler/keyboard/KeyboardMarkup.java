package org.example.controler.keyboard;

import java.util.List;

/**
 * Класс представляющий разметку клавиатуры
 */
public class KeyboardMarkup {
    public List<List<ButtonData>> keyboard;

    /**
     * устаналвиает разметку для клавиатуры
     */
    private void setKeyboardMarkup(List<List<ButtonData>> keyboard){
        this.keyboard=keyboard;
    }

    public KeyboardMarkup createKeyboard(List<List<ButtonData>> buttonRows) {
        KeyboardMarkup keyboard = new KeyboardMarkup();
        keyboard.setKeyboardMarkup(buttonRows);
        return keyboard;
    }
}
