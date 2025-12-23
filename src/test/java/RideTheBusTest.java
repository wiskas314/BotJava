import org.example.controler.dto.ButtonData;
import org.example.controler.game.GameMessage;
import org.example.controler.game.GameResponse;
import org.example.controler.game.RideTheBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * тестирование игры
 */
public class RideTheBusTest {
    private RideTheBus rideTheBus;
    private static final String CHAT_ID ="12345";

    @BeforeEach
    void setUp(){
        rideTheBus = new RideTheBus();
        rideTheBus.setBetPlacedForTest();
    }

    /**
     * тест на старт игры
     */
    @Test
    void testStartGame(){
        GameResponse response=rideTheBus.startGame(CHAT_ID);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isGameOver());

        GameMessage message = response.getMessage();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(CHAT_ID,message.getChatId());

        String text = message.getText();
        Assertions.assertNotNull(text);
        Assertions.assertTrue(text.contains("Раунд 1"));
        Assertions.assertTrue(text.contains("Выберите цвет:"));

        Assertions.assertTrue(text.contains("♠️") || text.contains("♥️") ||
                text.contains("♦️") || text.contains("♣️") ||
                text.contains("🃏"));

        List<List<ButtonData>> keyboardButtons = message.getKeyboardButtons();
        Assertions.assertNotNull(keyboardButtons);
        Assertions.assertFalse(keyboardButtons.isEmpty());

        List<ButtonData> firstRow=keyboardButtons.get(0);
        Assertions.assertEquals(2,firstRow.size());


        ButtonData redButton = firstRow.get(0);
        Assertions.assertEquals("Красный", redButton.getText());
        Assertions.assertEquals("red", redButton.getCallbackData());


        ButtonData blackButton = firstRow.get(1);
        Assertions.assertEquals("Черный", blackButton.getText());
        Assertions.assertEquals("black", blackButton.getCallbackData());
    }
}
