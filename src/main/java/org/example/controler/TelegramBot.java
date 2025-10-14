package org.example.controler;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс телеграм-бота
 */
public class TelegramBot extends TelegramLongPollingBot {
    private Map<String, RideTheBus> activeGames;
    private final String botUsername;
    private final String botToken;
    private final MessageHandler messageHandler;
    private final CallBackHandler callbackHandler;


    /**
     * конструктор
     */
    public TelegramBot(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.callbackHandler = new CallBackHandler();
        this.messageHandler = new MessageHandler();


    }

    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            send(callbackHandler.handleCallback(update));
            return;
        }


        if (update.hasMessage() && update.getMessage().hasText()) {
            Message userMessage = update.getMessage();
            long chatId = userMessage.getChatId();
            String userName = getUsername(update);
            String text = userMessage.getText();

            if (text.equals("/play")) {
                send(messageHandler.addKeybord(update));
            } else {
                String responseText = messageHandler.handleMessage(text, userName);
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(chatId));
                message.setText(responseText);
                send(message);
            }
        }
    }

    /**
     * Отправляет сообщение
     */
    private void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

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

