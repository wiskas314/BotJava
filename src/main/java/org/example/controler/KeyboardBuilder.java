package org.example.controler;

import org.example.controler.dto.ButtonData;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {
    /**
     * Создание кнопок для выбора игры
     */
    public List<List<ButtonData>> createGameSelectionButtons() {
        List<List<ButtonData>> buttons = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("🎮 Ride the Bus", "ride_the_bus"));
        row.add(new ButtonData("🎮 Black Jack", "black_jack"));
        buttons.add(row);
        return buttons;
    }
    /**
     * Создает клавиатуру для ставок
     */
    public List<List<ButtonData>> createBetKeyboard() {
        List<List<ButtonData>> buttonRows = new ArrayList<>();

        List<ButtonData> row1 = new ArrayList<>();
        row1.add(new ButtonData("10", "bet_10"));
        row1.add(new ButtonData("50", "bet_50"));
        row1.add(new ButtonData("100", "bet_100"));
        buttonRows.add(row1);


        List<ButtonData> row2 = new ArrayList<>();
        row2.add(new ButtonData("200", "bet_200"));
        row2.add(new ButtonData("500", "bet_500"));
        buttonRows.add(row2);


        List<ButtonData> row3 = new ArrayList<>();
        row3.add(new ButtonData("1000", "bet_1000"));
        row3.add(new ButtonData("Всё", "bet_all"));
        buttonRows.add(row3);

        return buttonRows;
    }
    /**
     * Создает клавиатуру для пополнения баланса
     */
    public List<List<ButtonData>> createReplenishKeyboard() {
        List<List<ButtonData>> buttonRows = new ArrayList<>();

        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("Пополнить 1000 🪙", "add_balance_1000"));
        buttonRows.add(row);

        return buttonRows;
    }
}
