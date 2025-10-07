package org.example.controler;


public class MessageHandler {
    /**
     * обрабатывает текст входящего сообщения и возвращает текстовый ответ.
     */
    public String handleUpdate(String message, String userName) {
        if (!message.equals("")) {
            if (message.equals("/start")) {
                return startMessage(userName);
            } else if (message.equals("/help")) {
                return helpMessage();
            } else {
                return echoMessage(message);
            }
        } else {
            return ErrMessage();
        }
    }

    /**
     * Сообщение, которое отправится при ошибке обработки исходного сообщения
     */
    private String ErrMessage() {
        return "Ошибка обработки входных данных, проверьте что вы ввели текст!";
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

    /**
     * формирует справочное сообщение для команды /help
     * содержит список всех доступных команд с их описанием
     */
    private String helpMessage() {
        return """
                Вот список доступных команд:
                /start - Начать общение с ботом
                /help - Получить список команд""";
    }
}
