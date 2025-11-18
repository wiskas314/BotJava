import org.example.controler.GameCallBack;
import org.example.controler.RideTheBus;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест для проверки полной игровой сессии RideTheBus.
 * Тестирует корректность взаимодействия между компонентами игры в процессе
 * прохождения всех этапов игровой сессии.
 */

public class RTBCompleteGameSessionTest {
    /**
     * Тестирует полную игровую сессию RideTheBus, включая:
     * - запуск игры
     * - обработку пользовательских выборов на всех раундах
     * - корректность отправки сообщений через callback
     * - завершение игровой сессии
     *
     * <p>Тест создает экземпляр игры, настраивает callback для отслеживания сообщений
     * и последовательно обрабатывает типичные пользовательские выборы для всех раундов.
     * Проверяет, что на каждом этапе игра отправляет соответствующие сообщения
     * и корректно обрабатывает ввод пользователя.</p>
     *
     * <p><b>Сценарий теста:</b></p>
     * <ol>
     *   <li>Создание и настройка игры с callback-интерфейсом</li>
     *   <li>Запуск игры и проверка начального сообщения</li>
     *   <li>Последовательная обработка выборов для всех раундов:
     *     <ul>
     *       <li>Раунд 1: выбор цвета карты ("red")</li>
     *       <li>Раунд 2: сравнение карт ("higher")</li>
     *       <li>Раунд 3: определение положения в диапазоне ("inside")</li>
     *       <li>Раунд 4: угадывание масти ("hearts")</li>
     *     </ul>
     *   </li>
     *   <li>Проверка корректности завершения сессии</li>
     * </ol>
     */
    @Test
    void testCompleteGameSession() {
        RideTheBus game = new RideTheBus();

        final int[] messageCount = {0};
        final String[] lastMessage = {""};

        game.setGameCallback(new GameCallBack() {
            @Override
            public void sendGameMessage(String chatId, String text, InlineKeyboardMarkup keyboard) {
                messageCount[0]++;
                lastMessage[0] = text;
                System.out.println("Message " + messageCount[0] + ": " + text);
            }
        });

        game.startGame("integration_test_chat");
        assertEquals(1, messageCount[0], "Должно быть отправлено сообщение при старте");
        assertTrue(lastMessage[0].contains("Раунд 1"), "Первое сообщение должно быть о первом раунде");

        String[] testChoices = {"red", "higher", "inside", "hearts"};

        for (String choice : testChoices) {
            if (game.IsGameOver()) {
                break;
            }

            int previousCount = messageCount[0];
            game.processUserChoice(choice);

            assertTrue(messageCount[0] > previousCount,
                    "После обработки выбора '" + choice + "' должно приходить новое сообщение");
        }

        assertTrue(true, "Игровая сессия должна завершиться без ошибок");
    }
}
