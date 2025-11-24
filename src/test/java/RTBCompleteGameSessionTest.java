import org.example.controler.MessageSender;

import org.example.controler.db.UserService;
import org.example.controler.game.Game;
import org.example.controler.game.RideTheBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import  org.junit.jupiter.api.Assertions;

/**
 * Интеграционный тест для проверки полной игровой сессии RideTheBus.
 * Тестирует корректность взаимодействия между компонентами игры в процессе
 * прохождения всех этапов игровой сессии.
 */

public class RTBCompleteGameSessionTest {
    private UserService userService;
    private final Long TEST_CHAT_ID = 99992L;
    private final String TEST_USERNAME = "test_user_rtb";

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userService.getOrCreateUser(TEST_CHAT_ID, TEST_USERNAME);
        userService.payWinnings(TEST_CHAT_ID, 1000);
    }

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
     */
    @Test
    void testCompleteGameSession() {
        RideTheBus game = new RideTheBus();

        final int[] messageCount = {0};
        final String[] lastMessage = {""};
        final boolean[] gameCompleted = {false};

        game.setGameCallback(new MessageSender() {
            @Override
            public void sendMessage(String text, String chatId, InlineKeyboardMarkup keyboard) {
                messageCount[0]++;
                lastMessage[0] = text;
                System.out.println("Message " + messageCount[0] + ": " + text);

                // Отслеживаем завершение игры по содержанию сообщений
                if (text.contains("проиграли") || text.contains("поздравляем") ||
                        text.contains("прошли все раунды") || text.contains("Вы забрали выигрыш")) {
                    gameCompleted[0] = true;
                }
            }
        });

        game.startGame(TEST_CHAT_ID.toString());
        Assertions.assertEquals(1, messageCount[0], "Должно быть сообщение о необходимости ставки");
        Assertions.assertTrue(lastMessage[0].contains("Сделайте ставку"), "Должен быть запрос ставки: " + lastMessage[0]);

        game.processBet("bet_50");
        Assertions.assertTrue(messageCount[0] >= 2, "Должно быть сообщение о принятии ставки");
        Assertions.assertTrue(lastMessage[0].contains("Раунд 1"), "Должен начаться первый раунд: " + lastMessage[0]);

        String[] testChoices = {"red", "higher", "inside", "hearts"};

        for (String choice : testChoices) {
            if (gameCompleted[0]) {
                System.out.println("Игра завершилась на выборе: " + choice);
                break;
            }

            int previousCount = messageCount[0];
            game.processUserChoice(choice);

            Assertions.assertTrue(messageCount[0] > previousCount,
                    "После обработки выбора '" + choice + "' должно прийти новое сообщение. " +
                            "Было: " + previousCount + ", стало: " + messageCount[0] +
                            ". Последнее сообщение: " + lastMessage[0]);
        }

        // Проверяем, что игра корректно завершилась (по нашему флагу)
        Assertions.assertTrue(gameCompleted[0], "Игровая сессия должна завершиться после всех раундов. " +
                "Последнее сообщение: " + lastMessage[0] +
                ", всего сообщений: " + messageCount[0]);

        // Дополнительная проверка - должно быть сообщение о завершении игры
        Assertions.assertTrue(lastMessage[0].contains("проиграли") ||
                        lastMessage[0].contains("поздравляем") ||
                        lastMessage[0].contains("прошли все раунды") ||
                        lastMessage[0].contains("Вы забрали выигрыш"),
                "Должно быть сообщение о завершении игры: " + lastMessage[0]);
    }

    /**
     * Дополнительный тест для проверки досрочного выхода из игры
     */
    @Test
    void testEarlyExitGameSession() {
        RideTheBus game = new RideTheBus();

        final int[] messageCount = {0};
        final String[] lastMessage = {""};
        final boolean[] gameCompleted = {false};

        game.setGameCallback(new MessageSender() {
            @Override
            public void sendMessage(String text, String chatId, InlineKeyboardMarkup keyboard) {
                messageCount[0]++;
                lastMessage[0] = text;
                System.out.println("Message " + messageCount[0] + ": " + text);

                if (text.contains("Вы забрали выигрыш") || text.contains("проиграли")) {
                    gameCompleted[0] = true;
                }
            }
        });

        // Запускаем игру и делаем ставку
        game.startGame(TEST_CHAT_ID.toString());
        game.processBet("bet_50");

        // Проходим первый раунд
        game.processUserChoice("red");

        // Выходим досрочно на втором раунде
        game.processUserChoice("exit");

        // Проверяем, что игра завершилась с выводом выигрыша
        Assertions.assertTrue(gameCompleted[0], "Игра должна завершиться после досрочного выхода. " +
                "Последнее сообщение: " + lastMessage[0]);
        Assertions.assertTrue(lastMessage[0].contains("Вы забрали выигрыш"),
                "Должно быть сообщение о выводе выигрыша: " + lastMessage[0]);
    }
}