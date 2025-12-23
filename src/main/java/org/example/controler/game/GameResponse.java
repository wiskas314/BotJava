package org.example.controler.game;

/**
 * Ответ игры на действие пользователя
 */
public class GameResponse {
    /**
     * Сообщение для отправки пользователю, содержащее текст и разметку клавиатуры
     */
    private final GameMessage message;

    /**
     * Флаг, указывающий завершена ли игра после этого ответа
     */
    private final boolean gameOver;

    public GameResponse(GameMessage message, boolean gameOver) {
        this.message = message;
        this.gameOver = gameOver;
    }

    /**
     * Получить сообщение игры
     */
    public GameMessage getMessage() {
        return message;
    }

    /**
     * Проверить, завершена ли игра
     */
    public boolean isGameOver() {
        return gameOver;
    }
}
