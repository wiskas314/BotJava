package org.example.controler.handlers;

import org.apache.commons.lang3.math.NumberUtils;
import org.example.controler.BalanceService;
import org.example.controler.db.UserService;
import org.example.controler.game.BlackJack;
import org.example.controler.game.Game;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.game.RideTheBus;
import org.example.controler.dto.ButtonData;
import org.example.controler.dto.CallbackData;
import org.example.controler.game.GameResponse;
import org.example.controler.dto.KeyboardMarkup;
import org.example.controler.tasks.TaskService;


import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * обработчик callback
 */
public class CallbackHandler {
    private final Map<String, Game> activeGames;
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private final TaskService taskService;
    private final TaskCallBackHandler taskCallBackHandler;
    private final BalanceService balanceService;
    private final UserService userService;

    public CallbackHandler(Map<String, Game> activeGames, MessageSender messageSender, KeyboardFactory keyboardFactory,
                           TaskService taskService, UserService userService, BalanceService balanceService,
                           TaskCallBackHandler taskCallBackHandler) {

        this.activeGames = activeGames;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
        this.taskService = taskService;
        this.userService = userService;
        this.taskCallBackHandler = taskCallBackHandler;
        this.balanceService = balanceService;
    }
    /**
     * Обработка callback данных
     */
    public void handleCallback(CallbackData callbackData) {
        String chatId = callbackData.getChatId();
        String callback = callbackData.getCallbackData();
        System.out.println("Мы обрабатываем просто колбэк");

        if (callback.equals("ride_the_bus")) {
            startRideTheBus(chatId);
            return;
        }
        if (callback.equals("black_jack")) {
            startBlackJack(chatId);
            return;
        }
        if (callback.startsWith("task_")) {
            taskCallBackHandler.handleTaskSettingsCallback(NumberUtils.toLong(chatId), callback);
            return;
        }

        if (callback.equals("black_jack_stat")) {
            sendBJStat(chatId);
            return;
        }
        if (callback.equals("ride_the_bus_stat")) {
            sendRTBStat(chatId);
            return;
        }

        if (callback.startsWith("bet_")) {
            placeBet(chatId, callback);
            return;
        }

        if (callback.equals("add_balance_1000")) {
            balanceService.handleBalanceReplenishment(chatId);
            return;
        }

        Game activeGame = activeGames.get(chatId);
        if (activeGame != null) {
            handleActiveGameCallback(activeGame, chatId,callback);
        }
    }

    /**
     * Размещение ставки
     */
    private void placeBet(String chatId, String callback){
        Game game = activeGames.get(chatId);
        if (game != null) {
            GameResponse response = game.processBet(callback);
            handleGameResponse(response);
        }
    }

    /**
     * Отправка статистики Black Jack
     */
    private void sendBJStat(String chatId){
        String text = "Количество побед - поражений: " + String.valueOf(userService.getBjWins(NumberUtils.toLong(chatId))) + "-" +
                String.valueOf(userService.getBjLosses(NumberUtils.toLong(chatId))) + "\n" +
                "Выиграно-проиграно:  " + String.valueOf(userService.getBjEarned(NumberUtils.toLong(chatId))) + "-" +
                String.valueOf(userService.getBjLost(NumberUtils.toLong(chatId)));
        messageSender.sendMessage(text, chatId, null);
    }

    /**
     * Отправка статистики Ride The Bus
     */
    private void sendRTBStat(String chatId){
        String text = "Количество побед - поражений:  " + String.valueOf(userService.getRtbWins(NumberUtils.toLong(chatId))) + "-" +
                String.valueOf(userService.getRtbLosses(NumberUtils.toLong(chatId))) + "\n" +
                "Выиграно-проиграно:  " + String.valueOf(userService.getRtbEarned(NumberUtils.toLong(chatId))) + "-" +
                String.valueOf(userService.getRtbLost(NumberUtils.toLong(chatId)));
        messageSender.sendMessage(text, chatId, null);
    }

    /**
     * Запуск игры Black Jack
     */
    private void startBlackJack(String chatId) {
        activeGames.remove(chatId);
        Game game = new BlackJack();
        activeGames.put(chatId, game);

        GameResponse response = game.startGame(chatId);
        handleGameResponse(response);
    }

    /**
     * Запуск игры Ride The Bus
     */
    private void startRideTheBus(String chatId) {
        activeGames.remove(chatId);
        Game game = new RideTheBus();
        activeGames.put(chatId, game);

        GameResponse response = game.startGame(chatId);
        handleGameResponse(response);
    }
    /**
     * Обработка callback во время активной игры
     */
    private void handleActiveGameCallback(Game activeGame, String chatId,String callbackData) {
        if (activeGame.getIsGameOver()) {
            activeGames.remove(chatId);
            return;
        }

        GameResponse response = activeGame.processUserChoice(callbackData);

        handleGameResponse(response);

        if (response.isGameOver()) {
            checkTaskProgressDelayed(NumberUtils.toLong(chatId));
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

    /**
     * Проверить прогресс заданий с задержкой (после игры)
     */
    private void checkTaskProgressDelayed(Long chatId) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                taskService.checkTaskProgressAfterGame(chatId);

            }
        }, 500);
    }
}
