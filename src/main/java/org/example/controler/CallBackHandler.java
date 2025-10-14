package org.example.controler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс для обработки информации с кнопок
 */
public class CallBackHandler {
    /**
     *Метод обрабатывает нажатия на кнопки инлайн-клавиатуры и возвращает
     * соответствующий ответ
     */
    public SendMessage handleCallback(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();


        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        switch (callbackData) {
            case "ride_the_bus":
                message.setText("Hello"+"\u2665\uFE0F");

                break;
            default:
                message.setText("❌ Неизвестная команда: " + callbackData);
                break;
        }

        return message;
    }
}
