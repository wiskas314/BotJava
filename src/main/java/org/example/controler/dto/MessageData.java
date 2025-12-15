package org.example.controler.dto;

/**
 * класс представляющий данные входящего текстового сообщения от пользователя
 */
public class MessageData {
    private final String chatId;
    private final String text;
    private final String userName;

    public MessageData(String chatId, String text, String userName) {
        this.chatId = chatId;
        this.text = text;
        this.userName = userName;
    }
    public String getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }
}
