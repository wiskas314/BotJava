package org.example.controler.db;

/**
 * Сервис для работы с пользователями
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Получение или создание пользователя
     */
    public User getOrCreateUser(Long chatId, String username) {
        return userRepository.createOrGetUser(chatId, username);
    }

    /**
     * Получение пользователя
     */
    public User getUser(Long chatId) {
        return userRepository.getUserByChatId(chatId);
    }

    /**
     * Получение баланса пользователя
     */
    public int getUserBalance(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getBalance():0;
    }

    /**
     * Обновление баланса
     */
    public boolean updateBalance(Long chatId, int newBalance) {
        return userRepository.updateBalance(chatId, newBalance);
    }

    /**
     * Изменение баланса на сумму
     */
    public boolean changeBalance(Long chatId, int amount) {
        return userRepository.changeBalance(chatId, amount);
    }

    /**
     * Проверка достаточности баланса для ставки
     */
    public boolean canPlaceBet(Long chatId, int betAmount) {
        return userRepository.hasSufficientBalance(chatId, betAmount);
    }

    /**
     * Размещение ставки (списание средств)
     */
    public boolean placeBet(Long chatId, int betAmount) {
        if (!canPlaceBet(chatId, betAmount)) {
            return false;
        }
        return changeBalance(chatId, -betAmount);
    }

    /**
     * Выплата выигрыша
     */
    public boolean payWinnings(Long chatId, int amount) {
        return changeBalance(chatId, amount);
    }
}
