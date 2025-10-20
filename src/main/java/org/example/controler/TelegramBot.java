package org.example.controler;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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
    public InlineKeyboardMarkup keyboard;
    /**
     * конструктор
     */
    public TelegramBot(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.messageHandler = new MessageHandler();
        this.activeGames = new HashMap<>();
        keyboard = null;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String chatId = callbackQuery.getMessage().getChatId().toString();
                String callbackData = callbackQuery.getData();

                if (callbackData.equals("ride_the_bus")) {
                    activeGames.remove(chatId);
                    RideTheBus game = new RideTheBus();
                    activeGames.put(chatId, game);
                    game.startGame(chatId, this);
                    return;
                }

                RideTheBus lateGame = activeGames.get(chatId);
                if (lateGame != null) {
                    if (lateGame.IsGameOver()) {
                        activeGames.remove(chatId);
                        return;
                    }
                    lateGame.processUserChoice(callbackData, this);
                    return;
                }
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                Message userMessage = update.getMessage();
                String chatId = String.valueOf(userMessage.getChatId());
                String userName = getUsername(update);
                String text = userMessage.getText();

                if (text.equals("/play")) {
                    KeyboardFactory keyboardFactory = new KeyboardFactory();
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Выберите игру:");
                    message.setReplyMarkup(keyboardFactory.createGameSelectionKeyboard());

                    sender(message);
                } else {
                    String responseText = messageHandler.handleMessage(text, userName);
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(responseText);
                    sender(message);
                }
            }
        } catch (Exception e) {
            System.err.println("Произошла ошибка при обработке обновления: " + e.getMessage());
        }
    }
    public void sendMessage(String text, String chatID, InlineKeyboardMarkup markup){
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(text);
        message.setReplyMarkup(markup);
        sender(message);
    }
    /**
     * Отправляет сообщение
     */
    protected void sender(SendMessage message) {
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