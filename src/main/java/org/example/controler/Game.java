package org.example.controler;

/**
 * Интерфейс для Black Jack и Ride the Bus
 */
public interface Game {

    /**
     * Начало игры
     */
    void startGame(String chatId, TelegramBot bot);

    /**
     * Метод для обработки выбора пользователя
     */
    void processUserChoice(String callbackData, TelegramBot bot);

    /**
     *Геттер состояния игры
     */
    boolean getIsGameOver();

    /**
     * Обработка ставки
     */
    void processBet(String callbackData, TelegramBot bot);
}