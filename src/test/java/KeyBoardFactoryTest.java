import org.example.controler.KeyboardFactory;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import org.junit.jupiter.api.Assertions.*;

/**
 * тестовый класс содержит тесты для метод создания клавиатур
 */
class KeyBoardFactoryTest {

    private KeyboardFactory keyboardFactory;

    /**
     * инициализация тестового окржуения перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        keyboardFactory = new KeyboardFactory();
    }

    /**
     * тестируем создание клавиатуры для выбора игры
     */
    @Test
    void testCreateGameSelectionKeyboard() {

        InlineKeyboardMarkup keyboard = keyboardFactory.createGameSelectionKeyboard();


        Assert.assertNotNull(keyboard);
        Assert.assertNotNull(keyboard.getKeyboard());
        Assert.assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        Assert.assertEquals(1, firstRow.size());

        InlineKeyboardButton button = firstRow.get(0);
        Assert.assertEquals("Ride the Bus", button.getText());
        Assert.assertEquals("ride_the_bus", button.getCallbackData());
    }

    /**
     * тестируем создание клавиатуры для первого раунда
     */
    @Test
    void testKeyboardFirstRound() {

        InlineKeyboardMarkup keyboard = keyboardFactory.keyboardFirstRound();


        Assert.assertNotNull(keyboard);
        Assert.assertNotNull(keyboard.getKeyboard());
        Assert.assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        Assert.assertEquals(2, firstRow.size());


        InlineKeyboardButton firstButton = firstRow.get(0);
        Assert.assertEquals("Красный", firstButton.getText());
        Assert.assertEquals("red", firstButton.getCallbackData());

        // Проверяем вторую кнопку
        InlineKeyboardButton secondButton = firstRow.get(1);
        Assert.assertEquals("Черный", secondButton.getText());
        Assert.assertEquals("black", secondButton.getCallbackData());
    }

    /**
     * тестируем создание клавиатуры для второго раунда
     */
    @Test
    void testCreateHigherLowerKeyboard() {

        InlineKeyboardMarkup keyboard = keyboardFactory.createHigherLowerKeyboard();


        Assert.assertNotNull(keyboard);
        Assert.assertNotNull(keyboard.getKeyboard());
        Assert.assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        Assert.assertEquals(2, firstRow.size());

        InlineKeyboardButton firstButton = firstRow.get(0);
        Assert.assertEquals("📈 Выше", firstButton.getText());
        Assert.assertEquals("higher", firstButton.getCallbackData());

        InlineKeyboardButton secondButton = firstRow.get(1);
        Assert.assertEquals("📉 Ниже", secondButton.getText());
        Assert.assertEquals("lower", secondButton.getCallbackData());
    }

    /**
     * тестируем создание клавиатуры для третьего раунда
     */
    @Test
    void testCreateRangeKeyboard() {

        InlineKeyboardMarkup keyboard = keyboardFactory.createRangeKeyboard();


        Assert.assertNotNull(keyboard);
        Assert.assertNotNull(keyboard.getKeyboard());
        Assert.assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        Assert.assertEquals(2, firstRow.size());

        InlineKeyboardButton firstButton = firstRow.get(0);
        Assert.assertEquals("📥 Внутри диапазона", firstButton.getText());
        Assert.assertEquals("inside", firstButton.getCallbackData());

        InlineKeyboardButton secondButton = firstRow.get(1);
        Assert.assertEquals("📤 Вне диапазона", secondButton.getText());
        Assert.assertEquals("outside", secondButton.getCallbackData());
    }

    /**
     * тестирование четвертой клавитуры
     */
    @Test
    void testCreateSuitGuessKeyboard() {

        InlineKeyboardMarkup keyboard = keyboardFactory.createSuitGuessKeyboard();


        Assert.assertNotNull(keyboard);
        Assert.assertNotNull(keyboard.getKeyboard());
        Assert.assertEquals(2, keyboard.getKeyboard().size());


        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        Assert.assertEquals(2, firstRow.size());

        InlineKeyboardButton firstRowFirstButton = firstRow.get(0);
        Assert.assertEquals("♥ Черви", firstRowFirstButton.getText());
        Assert.assertEquals("hearts", firstRowFirstButton.getCallbackData());

        InlineKeyboardButton firstRowSecondButton = firstRow.get(1);
        Assert.assertEquals("♦ Бубны", firstRowSecondButton.getText());
        Assert.assertEquals("diamonds", firstRowSecondButton.getCallbackData());


        List<InlineKeyboardButton> secondRow = keyboard.getKeyboard().get(1);
        Assert.assertEquals(2, secondRow.size());

        InlineKeyboardButton secondRowFirstButton = secondRow.get(0);
        Assert.assertEquals("♣ Трефы", secondRowFirstButton.getText());
        Assert.assertEquals("clubs", secondRowFirstButton.getCallbackData());

        InlineKeyboardButton secondRowSecondButton = secondRow.get(1);
        Assert.assertEquals("♠ Пики", secondRowSecondButton.getText());
        Assert.assertEquals("peaks", secondRowSecondButton.getCallbackData());
    }
    /**
     * Тестирует создание клавиатуры с пустым массивом кнопок
     * Ожидается что клавиатура будет создана но без строк
     */
    @Test
    void testCreateKeyboard_WithEmptyButtonsArray() {
        String[][][] emptyButtons = {};

        InlineKeyboardMarkup keyboard = keyboardFactory.createKeyboard(emptyButtons);

        Assert.assertNotNull(keyboard);
        Assert.assertNotNull(keyboard.getKeyboard());
        Assert.assertTrue(keyboard.getKeyboard().isEmpty());
    }
}