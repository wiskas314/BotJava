import org.example.controler.db.User;
import org.example.controler.db.UserService;
import org.junit.jupiter.api.Test;

import  org.junit.jupiter.api.Assertions;

/**
 * тестовый класс для проверки функциональности сервиса пользователей
 */
public class UserServiceTest {
    /**
     * тестирование полного цикла функциональности сервиса пользователей
     */
    @Test
    void testUserServiceFunctionality() {
        UserService userService = new UserService();


        User user = userService.getOrCreateUser(99999L, "test_user_999");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(99999L, user.getChatId());
        Assertions.assertEquals("test_user_999", user.getUsername());


        int balance = userService.getUserBalance(99999L);
        Assertions.assertTrue(balance >= 0);


        boolean success = userService.changeBalance(99999L, 100);
        Assertions.assertTrue(success);

        int newBalance = userService.getUserBalance(99999L);
        Assertions.assertEquals(balance + 100, newBalance);


        boolean canBet = userService.canPlaceBet(99999L, 50);
        Assertions.assertTrue(canBet);


        boolean betPlaced = userService.placeBet(99999L, 50);
        Assertions.assertTrue(betPlaced);


        boolean winningsPaid = userService.payWinnings(99999L, 200);
        Assertions.assertTrue(winningsPaid);
    }
}
