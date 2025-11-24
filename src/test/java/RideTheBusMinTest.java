import org.example.controler.game.Game;
import org.example.controler.game.RideTheBus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест проверяет что игра создается и работает
 */
public class RideTheBusMinTest {
    @Test
    void testBasicFunctionality() {
        Game game = new RideTheBus();

        assertDoesNotThrow(() -> {
            game.setGameCallback((chatId, text, keyboard) -> {});
            game.startGame("1");
            game.processUserChoice("red");
        });

        assertFalse(game.getIsGameOver());
    }
}

