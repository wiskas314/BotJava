package org.example.controler;

import org.example.controler.dto.GameResponse;

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
    boolean IsGameOver();
    /**
     * Получить тип игры
     */
    String getGameType();

}

