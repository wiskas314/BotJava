package org.example.controler.dto;

/**
 * Класс, представляющий данные callback-запроса от Telegram
 */
public class CallbackData {
    private final String chatId;
    private final String callbackData;

    public CallbackData(String chatId, String callbackData) {
        this.chatId = chatId;
        this.callbackData = callbackData;
    }
    public String getChatId(){
        return chatId;
    }
    public String getCallbackData(){
        return callbackData;
    }
}
