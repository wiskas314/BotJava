import org.example.controler.RideTheBus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест проверяет что игра создается и работает
 */
public class RideTheBusMinTest {
    @Test
    void testBasicFunctionality() {
        RideTheBus game = new RideTheBus();

        assertDoesNotThrow(() -> {
            game.setGameCallback((chatId, text, keyboard) -> {});
            game.startGame("test");
            game.processUserChoice("red");
        });

        assertFalse(game.IsGameOver());
    }
}

