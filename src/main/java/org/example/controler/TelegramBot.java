package org.example.controler;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * TelegramBot - основной класс для работы с Telegram Bot API
 */
public class TelegramBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private MessageHandler messageHandler;

    /**
     * Конструктор создает бота с указанными данными.
     */
    public TelegramBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.messageHandler = new MessageHandler();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String responseText = messageHandler.handleUpdate(update);
        long chatId = update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(responseText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
