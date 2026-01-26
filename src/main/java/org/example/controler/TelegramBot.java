package org.example.controler;

import org.apache.commons.lang3.math.NumberUtils;
import org.example.controler.keyboard.ButtonData;
import org.example.controler.dto.CallbackData;
import org.example.controler.keyboard.KeyboardMarkup;
import org.example.controler.dto.MessageData;
import org.example.controler.game.Game;
import org.example.controler.handlers.CallbackHandler;
import org.example.controler.handlers.MessageHandler;
import org.example.controler.handlers.TaskCallBackHandler;
import org.example.controler.keyboard.KeyboardBuilder;
import org.example.controler.tasks.TaskService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.example.controler.db.UserService;


import java.util.*;

/**
 * Класс телеграм-бота
 */
public class TelegramBot extends TelegramLongPollingBot implements MessageSender {
    private final String botUsername;
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;
    private final UserService userService;

    /**
     * конструктор
     */
    public TelegramBot(String botUsername, String botToken) {
        super(botToken);
        this.botUsername = botUsername;
        Map<String, Game> activeGames = new HashMap<>();
        userService = new UserService();
        KeyboardMarkup keyboardFactory = new KeyboardMarkup();
        KeyboardBuilder keyboardBuilder = new KeyboardBuilder();

        TaskService taskService = new TaskService(userService, this, keyboardFactory, keyboardBuilder);

        TaskCallBackHandler taskCallBackHandler = new TaskCallBackHandler(taskService, this,
                keyboardFactory);

        BalanceService balanceService = new BalanceService(userService, this, keyboardBuilder, keyboardFactory);

        this.messageHandler = new MessageHandler(this, keyboardFactory,
                taskService, balanceService, taskCallBackHandler);

        this.callbackHandler = new CallbackHandler(activeGames,this, keyboardFactory,
                taskService, userService, balanceService, taskCallBackHandler);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                handleCallbackUpdate(update);
                return;
            }
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleTextMessage(update);
                return;
            }
        } catch (Exception e) {
            System.err.println("Произошла ошибка при обработке обновления: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *обработка callback обновления
     */
    private void handleCallbackUpdate(Update update){
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String callbackData = callbackQuery.getData();

        userService.getOrCreateUser(NumberUtils.toLong(chatId), callbackQuery.getFrom().getUserName());

        CallbackData data = new CallbackData(chatId,callbackData);
        callbackHandler.handleCallback(data);
    }

    /**
     *обработка текстовых обновлений
     */
    private void handleTextMessage(Update update){
        Message userMessage = update.getMessage();
        String chatId = String.valueOf(userMessage.getChatId());
        String userName = getUsername(update);
        String text = userMessage.getText();

        MessageData messageData = new MessageData(chatId,text,userName);
        messageHandler.handleMessage(messageData);
    }

    @Override
    public void sendMessage(String text, String chatID, KeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(text);
        message.setReplyMarkup(convertToTelegramKeyboard(markup));
        sender(message);
    }

    /**
     *Конвертирует внутренее представление клавиатуры в формат телеграма
     */
    private InlineKeyboardMarkup convertToTelegramKeyboard(KeyboardMarkup markup){
        if(markup == null || markup.keyboard ==null){
            return null;
        }
        InlineKeyboardMarkup tgKeyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> telegramRows=new ArrayList<>();

        for(List<ButtonData> row:markup.keyboard){
            List<InlineKeyboardButton> telegramRow=new ArrayList<>();

            for (ButtonData buttonData:row){
                InlineKeyboardButton tgButton=new InlineKeyboardButton();
                tgButton.setText(buttonData.getText());
                tgButton.setCallbackData(buttonData.getCallbackData());
                telegramRow.add(tgButton);
            }
            telegramRows.add(telegramRow);
        }
        tgKeyboard.setKeyboard(telegramRows);
        return tgKeyboard;
    }

    /**
     * Отправляет сообщение
     */
    private void sender(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Не удалось отправить сообщение в чат " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Метод возвращает логин пользователя из сообщения
     */
    public String getUsername(Update update) {
        Chat chat = update.getMessage().getChat();
        String userName = (chat != null) ? chat.getFirstName() : "Неизвестный пользователь";
        return userName;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}
