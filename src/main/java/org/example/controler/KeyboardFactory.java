package org.example.controler;


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
    public InlineKeyboardMarkup createKeyboard(String[][][] buttonRows) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String[][] row : buttonRows){
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            for (String[] button : row){
                if(button.length>=2){
                    InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                    keyboardButton.setText(button[0]);
                    keyboardButton.setCallbackData(button[1]);
                    keyboardRow.add(keyboardButton);
                }
            }

            if(!keyboardRow.isEmpty()){
                rows.add(keyboardRow);
            }
        }
        keyboard.setKeyboard(rows);
        return keyboard;

    }
    public InlineKeyboardMarkup createGameSelectionKeyboard(){
        String[][][] buttons ={
                {{"Ride the Bus", "ride_the_bus"}}
        };
        return  createKeyboard(buttons);
    }

    /**
     * Создает клавиатуру для первого раунда игры - выбора цвета карты
     */
    public InlineKeyboardMarkup keyboardFirstRound(){
        String[][][] buttons ={
                {{"Красный", "red"}, {"Черный", "black"}}
        };
        return  createKeyboard(buttons);
    }

    /**
     * Создает клавиатуру для раунда выше/ниже"
     */
    public InlineKeyboardMarkup createHigherLowerKeyboard() {
        String[][][] buttons ={
                {{"📈 Выше", "higher"}, {"📉 Ниже", "lower"},{"Сохранить выигранное и выйти","exit"}}
        };
        return  createKeyboard(buttons);
    }

    /**
     * Создает клавиатуру для раунда диапазон
     */
    public InlineKeyboardMarkup createRangeKeyboard() {
        String[][][] buttons ={
                {{"📥 Внутри диапазона", "inside"}, {"📤 Вне диапазона", "outside"},{"Сохранить выигранное и выйти","exit"}}
        };
        return  createKeyboard(buttons);
    }

    /**
     * Создает клавиатуру для раунда угадай масть
     */
    public InlineKeyboardMarkup createSuitGuessKeyboard() {
        String[][][] buttons = {
                {{"♥ Черви", "hearts"}, {"♦ Бубны", "diamonds"}},
                {{"♣ Трефы", "clubs"}, {"♠ Пики", "peaks"},{"Сохранить выигранное и выйти","exit"}}
        };
        return createKeyboard(buttons);
    }

    /**
     * Создает клаивиатуру для ставок
     */
    public InlineKeyboardMarkup createBetKeyboard(){
        String[][][] buttons = {
                {{"10", "bet_10"}, {"50", "bet_50"}, {"100", "bet_100"}},
                {{"200", "bet_200"}, {"500", "bet_500"}},
                {{"1000", "bet_1000"}, {"Всё", "bet_all"}}
        };
        return createKeyboard(buttons);
    }

    /**
     *создает клавиатуру для пополнения баланса
     */
    public InlineKeyboardMarkup createReplenishKeyboard(){
        String[][][] buttons ={
            {{" Пополнить баланс +500", "add_balance_500"}}
        };
        return createKeyboard(buttons);
    }
}