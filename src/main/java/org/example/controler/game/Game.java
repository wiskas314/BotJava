package org.example.controler.game;

import org.example.controler.MessageSender;
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
    boolean getIsGameOver();

    /**
     * Обработка ставки
     */
    void processBet(String callbackData);

    /**
     * Метод для отправки сообщений в логике игры
     */
    void setGameCallback(MessageSender callback);



}