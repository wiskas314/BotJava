package org.example.controler.game;

/**
 * Интерфейс для Black Jack и Ride the Bus
 */
public interface Game {

    /**
     * Начало игры
     */
    GameResponse startGame(String chatId);

    /**
     * Метод для обработки выбора пользователя
     */
    GameResponse processUserChoice(String callbackData);

    /**
     *Геттер состояния игры
     */
    boolean getIsGameOver();

    /**
     * Обработка ставки
     */
    GameResponse processBet(String callbackData);

}