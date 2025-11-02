package org.example.controler;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.example.controler.db.UserService;
import org.example.controler.db.User;

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
    private UserService userService;
    private User user;

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
        userService = new UserService();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String chatId = callbackQuery.getMessage().getChatId().toString();
                Long userId = callbackQuery.getFrom().getId();
                String callbackData = callbackQuery.getData();

                userService.getOrCreateUser(userId, callbackQuery.getFrom().getUserName());

                if (callbackData.equals("ride_the_bus")) {
                    activeGames.remove(chatId);
                    RideTheBus game = new RideTheBus();
                    game.setUser(userId);
                    activeGames.put(chatId, game);
                    game.startGame(chatId, this);
                    return;
                }
                if(callbackData.equals("exit")){
                    RideTheBus game =activeGames.get(chatId);
                    if(game != null){
                        game.processUserChoice(callbackData,this);
                        return;
                    }
                }

                if (callbackData.startsWith("bet_")) {
                    RideTheBus game = activeGames.get(chatId);
                    if (game != null) {
                        game.processBet(callbackData, this);
                        return;
                    }
                }
                if (callbackData.equals("add_balance_500")){
                    Long userID = callbackQuery.getFrom().getId();
                    user = userService.getOrCreateUser(userID,callbackQuery.getFrom().getUserName());
                    KeyboardFactory keyboardFactory = new KeyboardFactory();
                    boolean success = userService.payWinnings(userID,500);

                    if (success) {
                        int newBalance = userService.getUserBalance(userID);
                        sendMessage(" Баланс пополнен на 500 \n Новый баланс: " + newBalance,
                                chatId, keyboardFactory.createGameSelectionKeyboard());
                    } else {
                        sendMessage(" Ошибка при пополнении баланса",
                                chatId, keyboardFactory.createGameSelectionKeyboard());
                    }
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
                Long chatId = userMessage.getChatId();
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
                    String responseText = messageHandler.handleMessage(text, userName,chatId);
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