package org.example.controler.dto;

import java.util.List;

/**
 * Класс представляющий разметку клавиатуры для телеграм-бота
 */
public class KeyboardMarkup {
    public List<List<ButtonData>> keyboard;

    /**
     * утсаналвиает разметку для клавиатуры
     */
    public void setKeyboardMarkup(List<List<ButtonData>> keyboard){
        this.keyboard=keyboard;
    }
}
