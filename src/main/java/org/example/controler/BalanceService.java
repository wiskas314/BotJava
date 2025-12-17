package org.example.controler;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.controler.db.User;
import org.example.controler.db.UserService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Сервис для работы с балансом пользователя
 */
public class BalanceService {
    private UserService userService;
    private MessageSender messageSender;
    private KeyboardFactory keyboardFactory;

    public BalanceService(UserService userService, MessageSender messageSender, KeyboardFactory keyboardFactory) {
        this.userService = userService;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
    }

    /**
     * Пополнение баланса пользователя
     */
    public boolean replenishBalance(Long userId, String username, int amount) {
        User user = userService.getOrCreateUser(userId, username);
        return userService.payWinnings(userId, amount);
    }

    /**
     * Получение баланса пользователя
     */
    public int getUserBalance(Long userId) {
        return userService.getUserBalance(userId);
    }

    /**
     * Обработка callback для пополнения баланса
     */
    public void handleBalanceReplenishment(String chatId, String username) {
        Long userId = NumberUtils.toLong(chatId);

        boolean success = replenishBalance(userId, username, 1000);

        if (success) {
            int newBalance = getUserBalance(userId);
            messageSender.sendMessage(
                    "Баланс пополнен на 1000\nНовый баланс: " + newBalance,
                    chatId,
                    keyboardFactory.createGameSelectionKeyboard()
            );
        } else {
            messageSender.sendMessage(
                    "Ошибка при пополнении баланса",
                    chatId,
                    keyboardFactory.createGameSelectionKeyboard()
            );
        }
    }

    /**
     * Обработка команды /balance
     */
    public void handleBalanceCommand(Long chatId) {
        int balance = getUserBalance(chatId);
        messageSender.sendMessage("Ваш баланс: " + balance, String.valueOf(chatId),
                keyboardFactory.createReplenishKeyboard());
    }
}
