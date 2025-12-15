import org.example.controler.KeyboardFactory;
import org.example.controler.dto.ButtonData;
import org.example.controler.dto.KeyboardMarkup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions.*;

/**
 * тестовый класс содержит тесты для метод создания клавиатур
 */
class KeyBoardFactoryTest {

    private KeyboardFactory keyboardFactory = new KeyboardFactory();

    @Test
    void testCreateKeyboard() {
        KeyboardFactory keyboardFactory = new KeyboardFactory();

        List<List<ButtonData>> buttonRows = new ArrayList<>();

        List<ButtonData> row1 = new ArrayList<>();
        row1.add(new ButtonData("Button 1", "callback_1"));
        row1.add(new ButtonData("Button 2", "callback_2"));

        List<ButtonData> row2 = new ArrayList<>();
        row2.add(new ButtonData("Button 3", "callback_3"));

        buttonRows.add(row1);
        buttonRows.add(row2);


        KeyboardMarkup keyboardMarkup = keyboardFactory.createKeyboard(buttonRows);

        Assertions.assertNotNull(keyboardMarkup);
        Assertions.assertNotNull(keyboardMarkup.keyboard);
        Assertions.assertEquals(2, keyboardMarkup.keyboard.size());

        List<ButtonData> firstRow = keyboardMarkup.keyboard.get(0);
        Assertions.assertEquals(2, firstRow.size());
        Assertions.assertEquals("Button 1", firstRow.get(0).getText());
        Assertions.assertEquals("callback_1", firstRow.get(0).getCallbackData());
        Assertions.assertEquals("Button 2", firstRow.get(1).getText());
        Assertions.assertEquals("callback_2", firstRow.get(1).getCallbackData());

        List<ButtonData> secondRow = keyboardMarkup.keyboard.get(1);
        Assertions.assertEquals(1, secondRow.size());
        Assertions.assertEquals("Button 3", secondRow.get(0).getText());
        Assertions.assertEquals("callback_3", secondRow.get(0).getCallbackData());
    }

    /**
     * тест на создание клавиатуры для выбора игры
     */
    @Test
    void testCreateGameSelectionKeyboard() {
        KeyboardFactory keyboardFactory = new KeyboardFactory();

        List<List<ButtonData>> buttonRows = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("🎮 Ride the Bus", "ride_the_bus"));
        buttonRows.add(row);

        KeyboardMarkup keyboardMarkup = keyboardFactory.createKeyboard(buttonRows);

        Assertions.assertNotNull(keyboardMarkup);
        Assertions.assertNotNull(keyboardMarkup.keyboard);
        Assertions.assertEquals(1, keyboardMarkup.keyboard.size());

        List<ButtonData> buttons = keyboardMarkup.keyboard.get(0);
        Assertions.assertEquals(1, buttons.size());

        ButtonData button = buttons.get(0);
        Assertions.assertEquals("🎮 Ride the Bus", button.getText());
        Assertions.assertEquals("ride_the_bus", button.getCallbackData());
    }
}