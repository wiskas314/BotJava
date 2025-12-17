package org.example.controler.game;

/**
 * Ответ игры на действие пользователя
 */
public class GameResponse {
    private final GameMessage message;
    private final boolean gameOver;

    public GameResponse(GameMessage message, boolean gameOver) {
        this.message = message;
        this.gameOver = gameOver;
    }

    public GameMessage getMessage() {
        return message;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
