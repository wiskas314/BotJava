package org.example.controler;

public interface Game {
    void startGame(String chatId, TelegramBot bot);
    void processUserChoice(String callbackData, TelegramBot bot);
    boolean getIsGameOver();
}