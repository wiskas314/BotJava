import org.example.controler.db.DBConfig;
import org.example.controler.db.User;
import org.example.controler.db.UserRepository;
import org.example.controler.db.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import  org.junit.jupiter.api.Assertions;


import java.util.ArrayList;
import java.util.List;

/**
 * тестовый класс для проверки функциональности сервиса пользователей
 */
public class UserServiceTest {
    private static final Long TEST_CHAT_ID = 99999L;
    private static final String TEST_USERNAME = "test_user_999";
    private UserService userService;
    private List<Long> testUserIds;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        testUserIds = new ArrayList<>();
        userService.getOrCreateUser(TEST_CHAT_ID, TEST_USERNAME);
        clearTestData();
    }

    @AfterEach
    void tearDown() {
        clearTestData();
    }

    /**
     * Очистка тестовых данных через существующие методы
     */
    private void clearTestData() {

        for (Long chatId : testUserIds) {
            try {

                int currentEarned = userService.getUserEarned(chatId);
                if (currentEarned != 0) {
                    userService.changeEarned(chatId, -currentEarned);
                }
            } catch (Exception e) {

            }
        }
        testUserIds.clear();
    }

    /**
     * Создание тестового пользователя с заданным заработком через существующие методы
     */
    private void createTestUserWithEarned(Long chatId, String username, int earned) {

        User user = userService.getOrCreateUser(chatId, username);
        testUserIds.add(chatId);


        int currentEarned = userService.getUserEarned(chatId);
        userService.changeEarned(chatId, earned - currentEarned);
    }

    /**
     * Получение только тестовых пользователей из топа
     */
    private List<User> getTestUsersFromTop(List<User> topPlayers) {
        List<User> testUsers = new ArrayList<>();
        for (User user : topPlayers) {
            if (testUserIds.contains(user.getChatId())) {
                testUsers.add(user);
            }
        }
        return testUsers;
    }
    /**
     * Тестирует корректность порядка сортировки пользователей в топе по убыванию заработка
     * Также проверяет, что пользователь с нулевым заработком не включается в топ
     */
    @Test
    void testGetTopPlayersByEarned_MultipleUsersOrder() {

        createTestUserWithEarned(100001L, "test_user_high", 1000);
        createTestUserWithEarned(100002L, "test_user_medium", 500);
        createTestUserWithEarned(100003L, "test_user_low", 100);
        createTestUserWithEarned(100004L, "test_user_zero", 0);


        List<User> result = userService.getTopPlayersByEarned(10);
        List<User> testUsers = getTestUsersFromTop(result);


        Assert.assertEquals(3, testUsers.size());


        Assert.assertEquals(1000, testUsers.get(0).getEarned());
        Assert.assertEquals(500, testUsers.get(1).getEarned());
        Assert.assertEquals(100, testUsers.get(2).getEarned());
    }
    /**
     * Тестирует работу ограничения количества возвращаемых записей в топе игроков
     */
    @Test
    void testGetTopPlayersByEarned_LimitWorks() {

        for (int i = 1; i <= 15; i++) {
            createTestUserWithEarned(100000L + i, "test_user_" + i, i * 100);
        }


        List<User> result = userService.getTopPlayersByEarned(5);
        List<User> testUsers = getTestUsersFromTop(result);


        Assert.assertEquals(5, testUsers.size());


        Assert.assertEquals(1500, testUsers.get(0).getEarned());
        Assert.assertEquals("test_user_15", testUsers.get(0).getUsername());
    }
    /**
     * Проверяет, что только что созданный пользователь имеет нулевое значение earned
     */
    @Test
    void testGetUserEarned_NewUser() {

        Long chatId = 100001L;
        userService.getOrCreateUser(chatId, "new_user");
        testUserIds.add(chatId);


        int earned = userService.getUserEarned(chatId);


        Assert.assertEquals(0, earned);
    }
    /**
     * Тестирует изменение заработка пользователя после имитации выигрыша
     * и проверяет, что метод getUserEarned возвращает обновленное значение.
     */
    @Test
    void testGetUserEarned_AfterWinning() {

        Long chatId = 100001L;
        userService.getOrCreateUser(chatId, "winner");
        testUserIds.add(chatId);


        userService.changeEarned(chatId, 500);


        int earned = userService.getUserEarned(chatId);


        Assert.assertEquals(500, earned);
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
