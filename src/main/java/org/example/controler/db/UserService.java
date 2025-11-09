package org.example.controler.db;

import java.util.List;

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
     * Получение топа игроков по параметру earned
     */
    public List<User> getTopPlayersByEarned(int limit) {
        return userRepository.getTopPlayersByEarned(limit);
    }

    /**
     * Получение позиции игрока в топе по earned
     */
    public int getPlayerRankByEarned(Long chatId) {
        return userRepository.getPlayerRankByEarned(chatId);
    }

    /**
     *получение того сколько заработал пользователь
     */
    public int getUserEarned(Long chatId){
        User user = getUser(chatId);
        return user!=null?user.getEarned():0;
    }
    /**
     * Получение кол-ва побед Black Jack
     */
    public int getBjWins(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getBjWins():0;
    }

    /**
     * Получение кол-ва поражений Black Jack
     */
    public int getBjLosses(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getBjLosses():0;
    }

    /**
     * Получение общего выигрыша Black Jack
     */
    public int getBjEarned(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getBjEarned():0;
    }

    /**
     * Получение общего проигрыша Black Jack
     */
    public int getBjLost(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getBjLost():0;
    }

    /**
     * Получение кол-ва побед Ride the Bus
     */
    public int getRtbWins(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getRtbWins():0;
    }

    /**
     * Получение кол-ва поражений Ride the Bus
     */
    public int getRtbLosses(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getRtbLosses():0;
    }

    /**
     * Получение общего выигрыша Ride the Bus
     */
    public int getRtbEarned(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getRtbEarned():0;
    }

    /**
     * Получение общего проигрыша Ride the Bus
     */
    public int getRtbLost(Long chatId) {
        User user= getUser(chatId);
        return user!=null?user.getRtbLost():0;
    }

    /**
     * Изменение баланса на сумму
     */
    public boolean changeBalance(Long chatId, int amount) {
        return userRepository.changeBalance(chatId, amount);
    }

    /**
     * иземенение общего заработка
     */
    public boolean changeEarned(Long chatId, int amount){return userRepository.changeEarned(chatId, amount);}

    /**
     * Изменение кол-ва побед и выигрыша для Black Jack
     */
    public boolean changeWinsAndEarnedBlackJack(Long chatId, int amount){return userRepository.changeWinsAndEarnedBlackJack(chatId, amount);}

    /**
     * Изменение кол-ва поражений и проигрыша для Black Jack
     */
    public boolean changeLossesAndLostBlackJack(Long chatId, int amount){return userRepository.changeLossesAndLostBlackJack(chatId, amount);}

    /**
     * Изменение кол-ва побед и выигрыша для Ride the Bus
     */
    public boolean changeWinAndEarnedRideTheBus(Long chatId, int amount){return userRepository.changeWinAndEarnedRideTheBus(chatId, amount);}

    /**
     * Изменение кол-ва поражений и проигрыша для Ride the Bus
     */
    public boolean changeLossesAndLostRideTheBus(Long chatId, int amount){return userRepository.changeLossesAndLostRideTheBus(chatId, amount);}

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
