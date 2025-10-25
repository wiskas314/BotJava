package org.example.controler.db;

public class User {
    private final Long chatId;
    private final String username;
    private int balance;

    public User(Long chatId, String username, int balance) {
        this.chatId = chatId;
        this.username = username;
        this.balance =balance;
    }
    public Long getChatId() { return chatId; }
    public String getUsername() { return username; }
    public int getBalance() { return balance; }

    @Override
    public String toString() {
        return String.format("User{chatId=%d, username='%s', balance=%d}",
                chatId, username, balance);
    }
}
