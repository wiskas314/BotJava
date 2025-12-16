package org.example.controler.handlers;

import org.example.controler.Game;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.RideTheBus;
import org.example.controler.dto.ButtonData;
import org.example.controler.dto.CallbackData;
import org.example.controler.dto.GameResponse;
import org.example.controler.dto.KeyboardMarkup;
import java.util.List;
import java.util.Map;

/**
 * обработчик callback
 */
public class CallbackHandler {
    private final Map<String, Game> activeGames;
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;

    public CallbackHandler(Map<String, Game> activeGames, MessageSender messageSender, KeyboardFactory keyboardFactory) {
        this.activeGames = activeGames;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
    }
    /**
     * Обработка callback данных
     */
    public void handleCallback(CallbackData callbackData) {
        String chatId = callbackData.getChatId();
        String callback = callbackData.getCallbackData();

        if (callback.equals("ride_the_bus")) {
            startRideTheBus(chatId);
            return;
        }

        Game activeGame = activeGames.get(chatId);
        if (activeGame != null) {
            handleActiveGameCallback(activeGame, chatId,callback);
        }
    }

    /**
     * Запуск игры Ride The Bus
     */
    private void startRideTheBus(String chatId) {
        activeGames.remove(chatId);
        RideTheBus game = new RideTheBus();
        activeGames.put(chatId, game);

        GameResponse response = game.startGame(chatId);
        handleGameResponse(response);
    }
    /**
     * Обработка callback во время активной игры
     */
    private void handleActiveGameCallback(Game activeGame, String chatId,String callbackData) {
        if (activeGame.IsGameOver()) {
            activeGames.remove(chatId);
            return;
        }

        GameResponse response = activeGame.processUserChoice(callbackData);

        handleGameResponse(response);

        if (response.isGameOver()) {
            activeGames.remove(chatId);
        }
    }
    /**
     * Обработка ответа от игры
     */
    private void handleGameResponse(GameResponse response) {
        if (response.getMessage() != null) {
            String chatId = response.getMessage().getChatId();
            String text = response.getMessage().getText();
            List<List<ButtonData>> buttons = response.getMessage().getKeyboardButtons();

            KeyboardMarkup keyboard = null;
            if (buttons != null && !buttons.isEmpty()) {
                keyboard = keyboardFactory.createKeyboard(buttons);
            }

            messageSender.sendMessage(text, chatId, keyboard);
        }
    }
}
