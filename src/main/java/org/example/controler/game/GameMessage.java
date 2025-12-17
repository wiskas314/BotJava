package org.example.controler.game;

import org.example.controler.dto.ButtonData;

import java.util.List;

/**
 * класс представляющий сообщение игры, которое будет отправлено пользователю
 */
public class GameMessage {
    private final String chatId;
    private final String text;
    private final List<List<ButtonData>> keyboardButtons;

    public GameMessage(String chatId, String text, List<List<ButtonData>> keyboardButtons) {
        this.chatId = chatId;
        this.text = text;
        this.keyboardButtons = keyboardButtons;
    }
    public String getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }

    public List<List<ButtonData>> getKeyboardButtons() {
        return keyboardButtons;
    }
}
