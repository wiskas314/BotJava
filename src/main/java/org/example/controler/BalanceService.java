package org.example.controler;
import org.apache.commons.lang3.math.NumberUtils;
import org.example.controler.db.UserService;
import org.example.controler.dto.KeyboardMarkup;
import org.example.controler.game.GameMessage;
import org.example.controler.game.GameResponse;

/**
 * Сервис для работы с балансом пользователя
 */
public class BalanceService {
    private UserService userService;
    private MessageSender messageSender;
    private KeyboardBuilder keyboardBuilder;
    private KeyboardFactory keyboardFactory;

    public BalanceService(UserService userService, MessageSender messageSender, KeyboardBuilder keyboardBuilder,
                          KeyboardFactory keyboardFactory) {
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
    public GameResponse handleBalanceReplenishment(String chatId) {
        Long userId = NumberUtils.toLong(chatId);

        boolean success = replenishBalance(userId, 1000);

        if (success) {
            int newBalance = userService.getUserBalance(userId);
            String messageText = "✅ Баланс пополнен на 1000 🪙\n💰 Новый баланс: " + newBalance + " 🪙";

            return new GameResponse(
                    new GameMessage(chatId, messageText, keyboardBuilder.createGameSelectionButtons()),
                    true
            );
        } else {
            String messageText = "❌ Ошибка при пополнении баланса";

            return new GameResponse(
                    new GameMessage(chatId, messageText, keyboardBuilder.createGameSelectionButtons()),
                    true
            );
        }
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
