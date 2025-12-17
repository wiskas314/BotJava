package org.example.controler.handlers;

import org.apache.commons.lang3.math.NumberUtils;
import org.example.controler.BalanceService;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;
import org.example.controler.dto.ButtonData;
import org.example.controler.dto.KeyboardMarkup;
import org.example.controler.dto.MessageData;
import org.example.controler.tasks.TaskService;

import java.util.ArrayList;
import java.util.List;

/**
 * класс для обрабатывания входящих сообщений и генерации ответа
 */
public class MessageHandler {
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private final TaskService taskService;
    private final BalanceService balanceService;
    private UserService userService;

    public MessageHandler(MessageSender messageSender,KeyboardFactory keyboardFactory, TaskService taskService,
                          UserService userService, BalanceService balanceService){
        this.messageSender =  messageSender;
        this.keyboardFactory = keyboardFactory;
        this.taskService = taskService;
        this.userService = userService;
        this.balanceService = balanceService;
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
            case "/balance":
                balanceService.handleBalanceCommand(NumberUtils.toLong(chatId));
                break;
            case "/statistic":
                handleStatisticCommand(chatId);
            case "/statistic_all":
                handleStatisticAllCommand(chatId);
            case "/task_settings":
                taskService.openTaskSettings(String.valueOf(chatId));
                break;
            case "/help":
                handleHelpCommand(chatId);
                break;

            default:
                if (isValidTimeFormat(text)){
                    taskService.handleTimeInput(NumberUtils.toLong(chatId), text);
            }

        }
    }

    private void handleStatisticAllCommand(String chatId) {
        int totalWins = userService.getBjWins(NumberUtils.toLong(chatId)) + userService.getRtbWins(NumberUtils.toLong(chatId));
        int totalLosses = userService.getBjLosses(NumberUtils.toLong(chatId)) + userService.getRtbLosses(NumberUtils.toLong(chatId));
        int totalEarned = userService.getBjEarned(NumberUtils.toLong(chatId)) + userService.getRtbEarned(NumberUtils.toLong(chatId));
        int totalLost = userService.getBjLost(NumberUtils.toLong(chatId)) + userService.getRtbLost(NumberUtils.toLong(chatId));

        String statisticText = "Побед - поражений: " + totalWins + " - " + totalLosses +
                "\nВыиграно - проиграно: " + totalEarned + " - " + totalLost;
        messageSender.sendMessage(statisticText, chatId, null);
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
    /**
     * Обработка команды /play
     */
    private void handlePlayCommand(String chatId){
        List<List<ButtonData>> buttons = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("Статистика Black Jack", "black_jack_stat"));
        row.add(new ButtonData("Статистика Ride The Bus", "ride_the_bus_stat"));
        buttons.add(row);

        KeyboardMarkup keyboard = keyboardFactory.createKeyboard(buttons);
        messageSender.sendMessage("Выберите игру:", chatId, keyboard);
    }


    private void handleStatisticCommand(String chatId){
        List<List<ButtonData>> buttons = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("🎮 Ride the Bus", "ride_the_bus"));
        buttons.add(row);

        KeyboardMarkup keyboard = keyboardFactory.createKeyboard(buttons);
        messageSender.sendMessage("Выберите игру:", chatId, keyboard);
    }

    /**
     * Обработка команды /help
     */
    private void handleHelpCommand(String chatId){
        messageSender.sendMessage("""
                Вот список доступных команд:
                /start - Начать общение с ботом
                /help - Получить список команд
                /play - Вызывает меню с выбором игр
                /balance - Показывает ваш баланс
                /statistic - Отображает вашу статистику
                /statistic_all - Отображает вашу общую статистику
                /top - Показывает топ 10 игроков
                /task_settings - Настройка ежедневных заданий""\";
                """,chatId,null);
    }
    /**
     * Проверка формата времени
     */
    private boolean isValidTimeFormat(String text) {
        if (text.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {return true;}
        return false;
    }
}
