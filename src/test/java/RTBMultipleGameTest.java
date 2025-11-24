
import org.example.controler.game.Game;
import org.example.controler.game.RideTheBus;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки корректности работы нескольких последовательных игровых сессий.
 * Проверяет устойчивость системы к многократному использованию и отсутствие side-effects между сессиями.
 */

public class RTBMultipleGameTest {
    /**
    * Тестирует возможность последовательного запуска нескольких игровых сессий без ошибок.
    */

    @Test
    void testMultipleGamesInRow() {
        Game game = new RideTheBus();

        game.setGameCallback((chatId, text, keyboard) -> {
        });

        final int numberOfGames = 3;

        for (int i = 0; i < numberOfGames; i++) {
            String chatId = "1" + i;

            assertDoesNotThrow(() -> {
                game.startGame(chatId);

                final int movesPerGame = 2;

                for (int j = 0; j < movesPerGame; j++) {
                    if (!game.getIsGameOver()) {
                        game.processUserChoice("red");
                    }
                }
            }, "Игровая сессия " + i + " завершилась с исключением");

            System.out.println("✓ Игровая сессия " + i + " завершена без исключений");
        }
    }
}
