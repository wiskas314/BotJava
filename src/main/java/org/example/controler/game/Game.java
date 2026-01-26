package org.example.controler.game;

/**
 * Интерфейс для игр
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