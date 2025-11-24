package org.example.controler.db;

/**
 * класс содержит информацию о пользователе, его балнас и имя с id
 */
public class User {
    private final Long chatId;
    private final String username;
    private int balance;

    /**
     * конструктор класса
     */
    public User(Long chatId, String username, int balance) {
        this.chatId = chatId;
        this.username = username;
        this.balance =balance;
    }

    /**
     *возвращает id пользователя
     */
    public Long getChatId() { return chatId; }

    /**
     *возвразащает имя пользователя
     */
    public String getUsername() { return username; }

    /**
     *возвращает баланс
     */
    public int getBalance() { return balance; }

    @Override
    public String toString() {
        return String.format("User{chatId=%d, username='%s', balance=%d}",
                chatId, username, balance);
    }
}
