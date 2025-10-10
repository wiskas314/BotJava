package org.example.controler;


import org.apache.commons.lang3.StringUtils;

/**
 * класс для обрабатывания входящих сообщений и генерации ответа
 */
public class MessageHandler {
    /**
     * обрабатывает текст входящего сообщения и возвращает текстовый ответ.
     */
    public String handleMessage(String message, String userName) {
        if (StringUtils.isNotEmpty(message)) {
            switch (message) {
                case "/start":
                    return startMessage(userName);
                case "/help":
                    return """
                Вот список доступных команд:
                /start - Начать общение с ботом
                /help - Получить список команд""";
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
    /**
     * Сообщение которое генерируется при начале диалога либо после команды /start
     */
    private String startMessage(String userName) {
        return "Привет, " + userName + "! Я бот, готовый помогать.\nЧтобы узнать, что я умею, введи /help";
    }

}
