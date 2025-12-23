package org.example.controler.game;

import org.example.controler.dto.ButtonData;

import java.util.List;

/**
 * Класс представляющий сообщение игры, которое будет отправлено пользователю
 */
public class GameMessage {
    /**
     * Идентификатор чата для отправки сообщения
     */
    private final String chatId;

    /**
     * Текст сообщения для отправки пользователю
     */
    private final String text;

    /**
     * Двумерный список кнопок для клавиатуры сообщения
     */
    private final List<List<ButtonData>> keyboardButtons;

    public GameMessage(String chatId, String text, List<List<ButtonData>> keyboardButtons) {
        this.chatId = chatId;
        this.text = text;
        this.keyboardButtons = keyboardButtons;
    }

    /**
     * Получить идентификатор чата
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * Получить текст сообщения
     */
    public String getText() {
        return text;
    }

    /**
     * Получить кнопки клавиатуры
     */
    public List<List<ButtonData>> getKeyboardButtons() {
        return keyboardButtons;
    }
}
