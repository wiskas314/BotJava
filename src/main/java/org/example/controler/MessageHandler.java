package org.example.controler;


import org.apache.commons.lang3.StringUtils;
import org.example.controler.db.UserService;

/**
 * Класс для обрабатывания входящих сообщений и генерации ответа
 */
public class MessageHandler {
    private final UserService userService;


    public MessageHandler() {
        this.userService = new UserService();
    }

    /**
     * Обрабатывает текст входящего сообщения и возвращает текстовый ответ.
     */
    public String handleMessage(String message, String userName,Long chatId) {
        if (StringUtils.isNotEmpty(message)) {
            switch (message) {
                case "/start":
                    return "Привет, " + userName + "! Я бот, готовый помочь скоротать время." +
                            "\nЧтобы узнать, что я умею, введи /help";
                case "/help":
                    return """
                Вот список доступных команд:
                /start - Начать общение с ботом
                /help - Получить список команд
                /play - Вызывает меню с выбором игр
                 /balance - Показывает ваш баланс""";
                case "/balance":
                    return "Ваш баланс " + String.valueOf(userService.getUserBalance(chatId));
                default:
                    return echoMessage(message);
            }
        } else {
            return "Ошибка обработки входных данных, проверьте что вы ввели текст!";
        }
    }

    /**
     * Генерируется эхо-сообщение пользователю
     */
    private String echoMessage(String messageText) {
        return "Вы написали: " + messageText;
    }

}
