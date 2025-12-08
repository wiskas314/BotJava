package org.example.controler.handlers;


import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.dto.MessageData;

/**
 * класс для обрабатывания входящих сообщений и генерации ответа
 */
public class MessageHandler {
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;

    public MessageHandler(MessageSender messageSender,KeyboardFactory keyboardFactory){
        this.messageSender =  messageSender;
        this.keyboardFactory = keyboardFactory;
    }
    /**
     * обрабатывает текст входящего сообщения и возвращает текстовый ответ.
     */
    public void handleMessage(MessageData messageData) {
        String chatId = messageData.getChatId();
        String text = messageData.getText();
        String userName = messageData.getUserName();

        switch (text){
            case "/start":
                handleStartCommand(chatId, userName);
                break;
            case "/play":
                handlePlayCommand(chatId);
                break;
            case "/help":
                handleHelpCommand(chatId);
                break;
        }
    }
    /**
     * Обработка команды /start
     */
    private void handleStartCommand(String chatId, String userName) {
        String welcomeMessage = String.format(
                "Привет, " + userName + "! Я бот, готовый помогать." +
        "\nЧтобы узнать, что я умею, введи /help"
        );
        messageSender.sendMessage(welcomeMessage, chatId, null);
    }

    private void handlePlayCommand(String chatId){
        messageSender.sendMessage("Выберите игру:", chatId,
                keyboardFactory.createGameSelectionKeyboard());
    }
    private void handleHelpCommand(String chatId){
        messageSender.sendMessage("""
                Вот список доступных команд:
                /start - Начать общение с ботом
                /help - Получить список команд
                /play - Вызывает меню с выбором игр
                """,chatId,null);
    }
}
