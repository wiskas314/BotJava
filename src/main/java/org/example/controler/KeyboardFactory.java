package org.example.controler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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
    public SendMessage createGameSelectionKeyboard(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton rideTheBusButton = new InlineKeyboardButton();

        rideTheBusButton.setText("Ride the Bus");
        rideTheBusButton.setCallbackData("ride_the_bus");

        rowInline.add(rideTheBusButton);
        rowsInline.add(rowInline);
        keyboard.setKeyboard(rowsInline);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите игру:");
        message.setReplyMarkup(keyboard);

        return message;
    }

    /**
     *Создает клавиатуру для первого раунда игры выбора цвета карты
     *
     */
    public SendMessage KeyboardFirstRound(Update update){
        String chatId = update.getMessage().getChatId().toString();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton Red = new InlineKeyboardButton();

        Red.setText("Красный");
        Red.setCallbackData("red");

        InlineKeyboardButton Black = new InlineKeyboardButton();

        Black.setText("Черный");
        Black.setCallbackData("black");

        rowInline.add(Red);
        rowInline.add(Black);
        rowsInline.add(rowInline);
        keyboard.setKeyboard(rowsInline);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите цвет:");
        message.setReplyMarkup(keyboard);

        return message;

    }

    /**
     *Создает клавиатуру для раунда выше/ниже"
     */
    public SendMessage createHigherLowerKeyboard(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton higherButton = new InlineKeyboardButton();
        higherButton.setText("📈 Выше");
        higherButton.setCallbackData("higher");
        row1.add(higherButton);

        InlineKeyboardButton lowerButton = new InlineKeyboardButton();
        lowerButton.setText("📉 Ниже");
        lowerButton.setCallbackData("lower");
        row1.add(lowerButton);

        rows.add(row1);
        keyboard.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите будет ли следующая карта старшей или младшей масти:");
        message.setReplyMarkup(keyboard);

        return message;
    }

    /**
     * Создает клавиатуру для раунда диапазон
     */
    public SendMessage createRangeKeyboard(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton insideButton = new InlineKeyboardButton();
        insideButton.setText("📥 Внутри диапазона");
        insideButton.setCallbackData("inside");
        row1.add(insideButton);


        InlineKeyboardButton outsideButton = new InlineKeyboardButton();
        outsideButton.setText("📤 Вне диапазона");
        outsideButton.setCallbackData("outside");
        row1.add(outsideButton);

        rows.add(row1);
        keyboard.setKeyboard(rows);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите будет ли следующая карта внутри или вне диапазона:");
        message.setReplyMarkup(keyboard);
        return message;
    }

    /**
     * Создает клавиатуру для раунда угадай масть
     */
    public SendMessage createSuitGuessKeyboard(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Первый ряд мастей
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton heartsButton = new InlineKeyboardButton();
        heartsButton.setText("♥ Черви");
        heartsButton.setCallbackData("hearts");
        row1.add(heartsButton);

        InlineKeyboardButton diamondsButton = new InlineKeyboardButton();
        diamondsButton.setText("♦ Бубны");
        diamondsButton.setCallbackData("diamonds");
        row1.add(diamondsButton);

        // Второй ряд мастей
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton clubsButton = new InlineKeyboardButton();
        clubsButton.setText("♣ Трефы");
        clubsButton.setCallbackData("clubs");
        row2.add(clubsButton);

        InlineKeyboardButton spadesButton = new InlineKeyboardButton();
        spadesButton.setText("♠ Пики");
        spadesButton.setCallbackData("peaks");
        row2.add(spadesButton);

        rows.add(row1);
        rows.add(row2);
        keyboard.setKeyboard(rows);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите какой масти будет слшедующая карта:");
        message.setReplyMarkup(keyboard);
        return message;
    }
}
