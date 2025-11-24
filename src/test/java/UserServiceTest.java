import org.example.controler.db.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import  org.junit.jupiter.api.Assertions;

/**
 * тестовый класс для проверки функциональности сервиса пользователей
 */
public class UserServiceTest {
    private UserService userService;
    private static final Long TEST_CHAT_ID = 99999L;
    private static final String TEST_USERNAME = "test_user_999";

    /**
     * Инициализация тестового окружения перед выполнением каждого теста.
     * Создает экземпляр UserService и тестового пользователя для последующих операций.
     */
    @BeforeEach
    void setUp() {
        userService = new UserService();
        userService.getOrCreateUser(TEST_CHAT_ID, TEST_USERNAME);
    }

    /**
     * тестирует успешное размещение ставки
     * проверяет, что при успешной ставке баланс уменьшается на сумму ставки
     */
    @Test
    void testPlaceBet() {
        userService.changeBalance(TEST_CHAT_ID, 100);
        int betAmount = 50;
        int initialBalance = userService.getUserBalance(TEST_CHAT_ID);

        boolean betPlaced = userService.placeBet(TEST_CHAT_ID, betAmount);
        int newBalance = userService.getUserBalance(TEST_CHAT_ID);

        Assertions.assertTrue(betPlaced);
        Assertions.assertEquals(initialBalance - betAmount, newBalance);
    }

    /**
     * тестирует выплату выигрыша пользователю
     * проверяет, что при выплате выигрыша баланс увеличивается на соответствующую сумму
     */
    @Test
    void testPayWinnings() {
        int initialBalance = userService.getUserBalance(TEST_CHAT_ID);
        int winnings = 200;

        boolean winningsPaid = userService.payWinnings(TEST_CHAT_ID, winnings);
        int newBalance = userService.getUserBalance(TEST_CHAT_ID);

        Assertions.assertTrue(winningsPaid);
        Assertions.assertEquals(initialBalance + winnings, newBalance);
    }

    /**
     * тестирует попытку размещения ставки при недостаточном балансе
     * проверяет, что система не позволяет сделать ставку и баланс не изменяется
     */
    @Test
    void testPlaceBetWithInsufficientBalance() {
        int currentBalance = userService.getUserBalance(TEST_CHAT_ID);
        int excessiveBetAmount = currentBalance + 1000;

        boolean betPlaced = userService.placeBet(TEST_CHAT_ID, excessiveBetAmount);
        int newBalance = userService.getUserBalance(TEST_CHAT_ID);

        Assertions.assertFalse(betPlaced);
        Assertions.assertEquals(currentBalance, newBalance);
    }

}
