package org.example.controler;

import org.example.controler.game.BlackJack;
import org.example.controler.game.Game;
import org.example.controler.game.RideTheBus;
import org.example.controler.tasks.TaskService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.example.controler.db.UserService;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Класс телеграм-бота
 */
public class TelegramBot extends TelegramLongPollingBot implements MessageSender {
    private final BalanceService balanceService;
    private final KeyboardFactory keyboardFactory;
    private Map<String, Game> activeGames;
    private final String botUsername;
    private final String botToken;
    private final MessageHandler messageHandler;
    public InlineKeyboardMarkup keyboard;
    private UserService userService;
    private final TaskService taskService;

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
        this.keyboardFactory = new KeyboardFactory();
        this.balanceService = new BalanceService(userService, this, keyboardFactory);
        this.taskService = new TaskService(userService, this, keyboardFactory);
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

                if (callbackData.startsWith("task_")) {
                    taskService.handleTaskSettingsCallback(userId, callbackData);
                    return;
                }

                if (callbackData.equals("ride_the_bus")) {
                    activeGames.remove(chatId);

                    RideTheBus game = new RideTheBus();
                    game.setGameCallback(this);
                    activeGames.put(chatId, game);
                    game.startGame(chatId);
                    return;
                }

                if (callbackData.equals("black_jack")) {
                    activeGames.remove(chatId);

                    BlackJack game = new BlackJack();
                    game.setGameCallback(this);
                    activeGames.put(chatId, game);
                    game.startGame(chatId);
                    return;
                }
                if (callbackData.equals("black_jack_stat")) {
                    String text = "Количество побед - поражений: " + String.valueOf(userService.getBjWins(userId)) + "-" +
                            String.valueOf(userService.getBjLosses(userId)) + "\n" +
                            "Выиграно-проиграно:  " + String.valueOf(userService.getBjEarned(userId)) + "-" +
                            String.valueOf(userService.getBjLost(userId));
                    sendMessage(text, chatId, null);
                    return;
                }

                if (callbackData.equals("ride_the_bus_stat")) {
                    String text = "Количество побед - поражений:  " + String.valueOf(userService.getRtbWins(userId)) + "-" +
                            String.valueOf(userService.getRtbLosses(userId)) + "\n" +
                            "Выиграно-проиграно:  " + String.valueOf(userService.getRtbEarned(userId)) + "-" +
                            String.valueOf(userService.getRtbLost(userId));
                    sendMessage(text, chatId, null);
                    return;
                }
                if (callbackData.equals("exit")) {
                    Game game = activeGames.get(chatId);
                    if (game != null) {
                        game.processUserChoice(callbackData);
                        checkTaskProgressDelayed(userId);
                    }
                    return;
                }

                if (callbackData.startsWith("bet_")) {
                    Game game = activeGames.get(chatId);
                    if (game != null) {
                        game.processBet(callbackData);
                    }
                    return;
                }

                if (callbackData.equals("add_balance_1000")) {
                    balanceService.handleBalanceReplenishment(callbackQuery);
                    return;
                }

                Game currentGame = activeGames.get(chatId);
                if (currentGame != null) {
                    currentGame.processUserChoice(callbackData);
                    if (currentGame.getIsGameOver()) {
                        checkTaskProgressDelayed(userId);
                        activeGames.remove(chatId);
                    }

                }
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                Message userMessage = update.getMessage();
                Long chatId = userMessage.getChatId();
                String userName = getUsername(update);
                String text = userMessage.getText();

                if (text.equals("/play")) {
                    sendMessage("Выберите игру", String.valueOf(chatId),
                            keyboardFactory.createGameSelectionKeyboard());
                } else if (text.equals("/balance")) {
                    balanceService.handleBalanceCommand(chatId);
                }else if (text.equals("/statistic")) {
                    sendMessage("Выберите по какой игре показать статистику:", String.valueOf(chatId),
                            keyboardFactory.createSelfStatFor());
                } else if (text.equals("/task_settings")) {
                    taskService.openTaskSettings(String.valueOf(chatId));
                } else if (isValidTimeFormat(text)) {
                    taskService.handleTimeInput(chatId, text);
                }else {
                    String responseText = messageHandler.handleMessage(text, userName, chatId);
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId.toString());

                    message.setText(responseText);
                    sender(message);
                }
            }
        } catch (Exception e) {
            System.err.println("Произошла ошибка при обработке обновления: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String text, String chatID, InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(text);
        message.setReplyMarkup(markup);
        sender(message);
    }

    /**
     * Отправляет сообщение
     */
    private void sender(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
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

    /**
     * Проверить прогресс заданий с задержкой (после игры)
     */
    private void checkTaskProgressDelayed(Long chatId) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Метод в тг боте, ща перейдет в тасксервис " + chatId);
                taskService.checkTaskProgressAfterGame(chatId);

            }
        }, 500);
    }

    /**
     * Проверка формата времени
     */
    private boolean isValidTimeFormat(String text) {
        if (text.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {return true;}
        return false;
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
