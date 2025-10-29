import org.example.controler.KeyboardFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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


        assertNotNull(keyboard);
        assertNotNull(keyboard.getKeyboard());
        assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        assertEquals(1, firstRow.size());

        InlineKeyboardButton button = firstRow.get(0);
        assertEquals("Ride the Bus", button.getText());
        assertEquals("ride_the_bus", button.getCallbackData());
    }

    /**
     * тестируем создание клавиатуры для первого раунда
     */
    @Test
    void testKeyboardFirstRound() {

        InlineKeyboardMarkup keyboard = keyboardFactory.keyboardFirstRound();


        assertNotNull(keyboard);
        assertNotNull(keyboard.getKeyboard());
        assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        assertEquals(2, firstRow.size());


        InlineKeyboardButton firstButton = firstRow.get(0);
        assertEquals("Красный", firstButton.getText());
        assertEquals("red", firstButton.getCallbackData());

        // Проверяем вторую кнопку
        InlineKeyboardButton secondButton = firstRow.get(1);
        assertEquals("Черный", secondButton.getText());
        assertEquals("black", secondButton.getCallbackData());
    }

    /**
     * тестируем создание клавиатуры для второго раунда
     */
    @Test
    void testCreateHigherLowerKeyboard() {

        InlineKeyboardMarkup keyboard = keyboardFactory.createHigherLowerKeyboard();


        assertNotNull(keyboard);
        assertNotNull(keyboard.getKeyboard());
        assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        assertEquals(2, firstRow.size());

        InlineKeyboardButton firstButton = firstRow.get(0);
        assertEquals("📈 Выше", firstButton.getText());
        assertEquals("higher", firstButton.getCallbackData());

        InlineKeyboardButton secondButton = firstRow.get(1);
        assertEquals("📉 Ниже", secondButton.getText());
        assertEquals("lower", secondButton.getCallbackData());
    }

    /**
     * тестируем создание клавиатуры для третьего раунда
     */
    @Test
    void testCreateRangeKeyboard() {

        InlineKeyboardMarkup keyboard = keyboardFactory.createRangeKeyboard();


        assertNotNull(keyboard);
        assertNotNull(keyboard.getKeyboard());
        assertEquals(1, keyboard.getKeyboard().size());

        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        assertEquals(2, firstRow.size());

        InlineKeyboardButton firstButton = firstRow.get(0);
        assertEquals("📥 Внутри диапазона", firstButton.getText());
        assertEquals("inside", firstButton.getCallbackData());

        InlineKeyboardButton secondButton = firstRow.get(1);
        assertEquals("📤 Вне диапазона", secondButton.getText());
        assertEquals("outside", secondButton.getCallbackData());
    }

    /**
     * тестирование четвертой клавитуры
     */
    @Test
    void testCreateSuitGuessKeyboard() {

        InlineKeyboardMarkup keyboard = keyboardFactory.createSuitGuessKeyboard();


        assertNotNull(keyboard);
        assertNotNull(keyboard.getKeyboard());
        assertEquals(2, keyboard.getKeyboard().size());


        List<InlineKeyboardButton> firstRow = keyboard.getKeyboard().get(0);
        assertEquals(2, firstRow.size());

        InlineKeyboardButton firstRowFirstButton = firstRow.get(0);
        assertEquals("♥ Черви", firstRowFirstButton.getText());
        assertEquals("hearts", firstRowFirstButton.getCallbackData());

        InlineKeyboardButton firstRowSecondButton = firstRow.get(1);
        assertEquals("♦ Бубны", firstRowSecondButton.getText());
        assertEquals("diamonds", firstRowSecondButton.getCallbackData());


        List<InlineKeyboardButton> secondRow = keyboard.getKeyboard().get(1);
        assertEquals(2, secondRow.size());

        InlineKeyboardButton secondRowFirstButton = secondRow.get(0);
        assertEquals("♣ Трефы", secondRowFirstButton.getText());
        assertEquals("clubs", secondRowFirstButton.getCallbackData());

        InlineKeyboardButton secondRowSecondButton = secondRow.get(1);
        assertEquals("♠ Пики", secondRowSecondButton.getText());
        assertEquals("peaks", secondRowSecondButton.getCallbackData());
    }
    /**
     * Тестирует создание клавиатуры с пустым массивом кнопок
     * Ожидается что клавиатура будет создана но без строк
     */
    @Test
    void testCreateKeyboard_WithEmptyButtonsArray() {
        String[][][] emptyButtons = {};

        InlineKeyboardMarkup keyboard = keyboardFactory.createKeyboard(emptyButtons);

        assertNotNull(keyboard);
        assertNotNull(keyboard.getKeyboard());
        assertTrue(keyboard.getKeyboard().isEmpty());
    }
}