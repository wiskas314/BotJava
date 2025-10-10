package org.example.controler;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * TelegramBot - основной класс для работы с Telegram Bot API
 */
public class TelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final MessageHandler messageHandler;

    public TelegramBot(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.messageHandler = new MessageHandler();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message userMessage = update.getMessage();
        long chatId = userMessage.getChatId();
        String userName = getUsername(update);
        String text= userMessage.getText();

        String responseText = messageHandler.handleMessage(text, userName);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(responseText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает текст из сообщения пользователя
     */
    private String getMessage(Update update) {
        String messageText = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageText = update.getMessage().getText();
        }
        return messageText;
    }

    /**
     * Метод возвращает логин пользователя из сообщения
     */
    public String getUsername(Update update){
        Chat chat = update.getMessage().getChat();
        String userName = (chat != null) ? chat.getFirstName() : "Неизвестный пользователь";
        return userName;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}

