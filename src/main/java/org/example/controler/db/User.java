package org.example.controler.db;

/**
 * Класс содержит информацию о пользователе, его балнас и имя с id
 */
public class User {
    private final Long chatId;
    private final String username;
    private int balance;
    private int earned;

    private int bjWins;
    private int bjLosses;
    private int bjEarned;
    private int bjLost;

    private int rtbWins;
    private int rtbLosses;
    private int rtbEarned;
    private int rtbLost;


    /**
     * Конструктор класса
     */
    public User(long chatId, String username, int balance, int earned, int bjWins, int bjLosses, int bjEarned, int bjLost, int rtbWins, int rtbLosses, int rtbEarned, int rtbLost) {
        this.chatId = chatId;
        this.username = username;
        this.balance = balance;
        this.earned = earned;

        this.bjWins = bjWins;
        this.bjLosses = bjLosses;
        this.bjEarned = bjEarned;
        this.bjLost = bjLost;
        this.rtbWins = rtbWins;
        this.rtbLosses = rtbLosses;
        this.rtbEarned = rtbEarned;
        this.rtbLost = rtbLost;
    }

    /**
     *Возвращает id пользователя
     */
    public Long getChatId() { return chatId; }

    /**
     *Возвращает имя пользователя
     */
    public String getUsername() { return username; }

    /**
     *Возвращает баланс
     */
    protected int getBalance() { return balance; }

    /**
     * возвращает сколько всего заработал
     */
    public int getEarned(){return earned;}

    /**
     * Возвращает кол-во побед Black Jack
     */
    protected int getBjWins() { return bjWins; }

    /**
     * Возвращает кол-во поражений Black Jack
     */
    protected int getBjLosses() { return bjLosses; }

    /**
     * Возвращает общий выигрыш Black Jack
     */
    protected int getBjEarned() { return bjEarned; }

    /**
     * Возвращает общий проигрыш Black Jack
     */
    protected int getBjLost() { return bjLost; }

    /**
     * Возвращает кол-во побед Ride the Bus
     */
    protected int getRtbWins() { return rtbWins; }

    /**
     * Возвращает кол-во поражений Ride the Bus
     */
    protected int getRtbLosses() { return rtbLosses; }

    /**
     * Возвращает общий выигрыш Ride the Bus
     */
    protected int getRtbEarned() { return rtbEarned; }

    /**
     * Возвращает общий проигрыш Ride the Bus
     */
    protected int getRtbLost() { return rtbLost; }


    @Override
    public String toString() {
        return String.format("User{chatId=%d, username='%s', balance=%d}",
                chatId, username, balance);
    }
}
