package org.example.controler;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.controler.db.UserService;
import org.example.controler.keyboard.KeyboardBuilder;
import org.example.controler.keyboard.KeyboardMarkup;

/**
 * Сервис для работы с балансом пользователя
 */
public class BalanceService {
    private UserService userService;
    private MessageSender messageSender;
    private KeyboardBuilder keyboardBuilder;
    private KeyboardMarkup keyboardFactory;

    public BalanceService(UserService userService, MessageSender messageSender, KeyboardBuilder keyboardBuilder,
                          KeyboardMarkup keyboardFactory) {
        this.userService = userService;
        this.messageSender = messageSender;
        this.keyboardBuilder = keyboardBuilder;
        this.keyboardFactory= keyboardFactory;
    }

    /**
     * Пополнение баланса пользователя
     */
    public boolean replenishBalance(Long userId, int amount) {
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
    public void handleBalanceReplenishment(String chatId) {
        Long userId = NumberUtils.toLong(chatId);
        String messageText = null;
        boolean success = replenishBalance(userId, 1000);

        if (success) {
            int newBalance = userService.getUserBalance(userId);
            messageText = "✅ Баланс пополнен на 1000 🪙\n💰 Новый баланс: " + newBalance + " 🪙";

        } else {
            messageText = "❌ Ошибка при пополнении баланса";

        }
        messageSender.sendMessage(messageText, String.valueOf(chatId),
                keyboardFactory.createKeyboard(keyboardBuilder.createGameSelectionButtons()));
    }

    /**
     * Обработка команды /balance
     */
    public void handleBalanceCommand(Long chatId) {
        int balance = getUserBalance(chatId);
        messageSender.sendMessage("Ваш баланс: " + balance, String.valueOf(chatId),
                 keyboardFactory.createKeyboard(keyboardBuilder.createReplenishKeyboard()));
    }
}
