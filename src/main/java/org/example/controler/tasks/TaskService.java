package org.example.controler.tasks;

import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для управления ежедневными заданиями
 */
public class TaskService {
    private final UserService userService;
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;


    private final Map<Long, TaskProgressState> taskStates = new ConcurrentHashMap<>();
    private final Map<Long, ActiveTaskInfo> activeTasks = new ConcurrentHashMap<>();
    private final Map<Long, TaskSettings> taskSettingsMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TaskService(UserService userService, MessageSender messageSender, KeyboardFactory keyboardFactory) {
        this.userService = userService;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;

        startDailyTaskScheduler();
    }

    /**
     * Получить состояние прогресса пользователя через UserService
     */
    private TaskProgressState getTaskProgressState(Long chatId) {
        return taskStates.computeIfAbsent(chatId, id -> {
            return new TaskProgressState(
                    userService.getBjWins(id),
                    userService.getBjLosses(id),
                    userService.getBjEarned(id),
                    userService.getBjLost(id),
                    userService.getRtbWins(id),
                    userService.getRtbLosses(id),
                    userService.getRtbEarned(id),
                    userService.getRtbLost(id),
                    userService.getUserBalance(id),
                    userService.getUserEarned(id)
            );
        });
    }

    /**
     * Проверить прогресс заданий после игры
     */
    public void checkTaskProgressAfterGame(Long chatId) {
        TaskProgressState oldState = getTaskProgressState(chatId);
        System.out.println("Мы уже в классе Таск сервис " + chatId);
        TaskProgressState newState = new TaskProgressState(
                userService.getBjWins(chatId),
                userService.getBjLosses(chatId),
                userService.getBjEarned(chatId),
                userService.getBjLost(chatId),
                userService.getRtbWins(chatId),
                userService.getRtbLosses(chatId),
                userService.getRtbEarned(chatId),
                userService.getRtbLost(chatId),
                userService.getUserBalance(chatId),
                userService.getUserEarned(chatId)
        );

        ActiveTaskInfo activeTask = activeTasks.get(chatId);
        if (activeTask != null && !activeTask.isCompleted()) {
            checkTaskProgress(chatId, activeTask, oldState, newState);
        }

        taskStates.put(chatId, newState);
    }

    /**
     * Проверить прогресс конкретного задания
     */
    private void checkTaskProgress(Long chatId, ActiveTaskInfo task,
                                   TaskProgressState oldState, TaskProgressState newState) {
        System.out.println("Проверяем чче там поменялось в базе данных " + chatId);
        int progress = 0;

        switch (task.getTaskType()) {
            case "WIN_BLACKJACK":
                progress = newState.getBjWins() - oldState.getBjWins();
                break;
            case "PLAY_BLACKJACK":
                progress = (newState.getBjWins() - oldState.getBjWins()) +
                        (newState.getBjLosses() - oldState.getBjLosses());
                break;
            case "EARN_BLACKJACK":
                progress = (newState.getBjEarned() - oldState.getBjEarned()) -
                        (newState.getBjLost() - oldState.getBjLost());
                if (progress < 0) progress = 0;
                break;
            case "WIN_RIDE_THE_BUS":
                progress = newState.getRtbWins() - oldState.getRtbWins();
                break;
            case "PLAY_RIDE_THE_BUS":
                progress = (newState.getRtbWins() - oldState.getRtbWins()) +
                        (newState.getRtbLosses() - oldState.getRtbLosses());
                break;
            case "EARN_RIDE_THE_BUS":
                progress = (newState.getRtbEarned() - oldState.getRtbEarned()) -
                        (newState.getRtbLost() - oldState.getRtbLost());
                if (progress < 0) progress = 0;
                break;
            case "EARN_ANY":
                progress = newState.getEarned() - oldState.getEarned();
                break;
        }

        if (progress > 0) {
            task.incrementProgress(progress);

            if (task.isCompleted()) {
                sendTaskCompletedNotification(chatId, task);
            }
        }
    }

    /**
     * Отправить уведомление о выполнении задания
     */
    private void sendTaskCompletedNotification(Long chatId, ActiveTaskInfo task) {
        String message = "🎉 *Задание выполнено!*\n\n" +
                "📝 " + task.getDescription() + "\n" +
                "💰 Награда: " + task.getReward() + " 🪙\n\n" +
                "Нажмите кнопку ниже, чтобы забрать награду!";

        InlineKeyboardMarkup keyboard = createTaskClaimKeyboard();
        messageSender.sendMessage(message, String.valueOf(chatId), keyboard);
    }

    /**
     * Открыть меню настроек заданий
     */
    public void openTaskSettings(String chatIdStr) {
        Long chatId = Long.valueOf(chatIdStr);
        TaskSettings settings = getOrCreateTaskSettings(chatId);

        String message = "⚙️ Настройка ежедневных заданий:\n\n" +
                "• Статус: [" + settings.getStatusInRussian() + "]\n" +
                "• Время получения: [" + settings.getFormattedTime() + "]\n" +
                "• Уровень сложности: [" + settings.getDifficultyInRussian() + "]";

        InlineKeyboardMarkup keyboard = createTaskSettingsKeyboard(settings);
        messageSender.sendMessage(message, chatIdStr, keyboard);
    }

    /**
     * Создать новое задание для пользователя
     */
    private void createNewTaskForUser(Long chatId) {
        TaskSettings settings = getOrCreateTaskSettings(chatId);

        if (!settings.isEnabled()) {
            return;
        }


        activeTasks.remove(chatId);


        ActiveTaskInfo newTask = new TaskGenerator().generateTask(
                chatId,
                settings.getDifficulty(),
                LocalDate.now()
        );

        if (newTask != null) {
            activeTasks.put(chatId, newTask);


            sendTaskToUser(chatId, newTask);
        }
    }

    /**
     * Отправить задание пользователю
     */
    private void sendTaskToUser(Long chatId, ActiveTaskInfo task) {
        TaskSettings settings = getOrCreateTaskSettings(chatId);
        String difficultyText = settings.getDifficulty().equals("EASY") ? "Легкая" : "Сложная";
        String emoji = settings.getDifficulty().equals("EASY") ? "🟢" : "🔴";

        String message = emoji + " *Ежедневное задание!* (Сложность: " + difficultyText + ")\n\n" +
                "📝 *Задание:* " + task.getDescription() + "\n" +
                "🎯 *Требуется:* " + task.getTargetValue() + "\n" +
                "💰 *Награда:* " + task.getReward() + " 🪙";

        InlineKeyboardMarkup keyboard = keyboardFactory.createGameSelectionKeyboard();
        messageSender.sendMessage(message, String.valueOf(chatId), keyboard);
    }

    /**
     * Забрать награду за задание
     */
    public void claimTaskReward(Long chatId) {
        ActiveTaskInfo task = activeTasks.get(chatId);

        if (task == null || !task.isCompleted()) {
            messageSender.sendMessage(
                    "❌ У вас нет выполненных заданий для получения награды",
                    String.valueOf(chatId),
                    null
            );
            return;
        }


        boolean success = userService.payWinnings(chatId, task.getReward());

        if (success) {
            int newBalance = userService.getUserBalance(chatId);


            userService.changeEarned(chatId, task.getReward());

            String message = "🎉 *Награда получена!*\n\n" +
                    "💰 +" + task.getReward() + " 🪙\n" +
                    "💎 Новый баланс: " + newBalance + " 🪙\n\n" +
                    "Задание будет обновлено завтра!";


            activeTasks.remove(chatId);

            messageSender.sendMessage(message, String.valueOf(chatId),
                    keyboardFactory.createGameSelectionKeyboard());
        } else {
            messageSender.sendMessage(
                    "❌ Ошибка при получении награды",
                    String.valueOf(chatId),
                    null
            );
        }
    }

    /**
     * Запуск ежедневного планировщика
     */
    private void startDailyTaskScheduler() {

        scheduler.scheduleAtFixedRate(() -> {
            LocalTime now = LocalTime.now();


            for (Long chatId : getAllUsersWithTasks()) {
                TaskSettings settings = getOrCreateTaskSettings(chatId);

                if (settings.isEnabled() &&
                        now.getHour() == settings.getNotificationHour() &&
                        now.getMinute() == settings.getNotificationMinute()) {


                    ActiveTaskInfo currentTask = activeTasks.get(chatId);
                    if (currentTask == null || !currentTask.getAssignedDate().equals(LocalDate.now())) {
                        createNewTaskForUser(chatId);
                    }
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Получить всех пользователей с активными заданиями
     */
    private Set<Long> getAllUsersWithTasks() {
        return new HashSet<>(taskSettingsMap.keySet());
    }

    /**
     * Получить или создать настройки (в памяти)
     */
    private TaskSettings getOrCreateTaskSettings(Long chatId) {
        return taskSettingsMap.computeIfAbsent(chatId, id -> new TaskSettings(id));
    }

    /**
     * Обновить настройки
     */
    private void updateTaskSettings(TaskSettings settings) {
        taskSettingsMap.put(settings.getChatId(), settings);
    }

    /**
     * Создать клавиатуру для получения награды
     */
    private InlineKeyboardMarkup createTaskClaimKeyboard() {
        return keyboardFactory.createTaskClaimKeyboard();
    }

    /**
     * Создать клавиатуру для настроек
     */
    private InlineKeyboardMarkup createTaskSettingsKeyboard(TaskSettings settings) {
        return keyboardFactory.createTaskSettingsKeyboard(
                settings.isEnabled(),
                settings.getFormattedTime(),
                settings.getDifficulty()
        );
    }

    /**
     * Обработка callback от настроек
     */
    public void handleTaskSettingsCallback(Long chatId, String callbackData) {
        TaskSettings settings = getOrCreateTaskSettings(chatId);

        switch (callbackData) {
            case "task_toggle":
                settings.setEnabled(!settings.isEnabled());
                updateTaskSettings(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_change_time":
                showTimeSelectionKeyboard(chatId);
                break;

            case "task_time_9":
                settings.setNotificationHour(9);
                settings.setNotificationMinute(0);
                updateTaskSettings(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_time_14":
                settings.setNotificationHour(14);
                settings.setNotificationMinute(0);
                updateTaskSettings(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_time_20":
                settings.setNotificationHour(20);
                settings.setNotificationMinute(0);
                updateTaskSettings(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_change_difficulty":
                String newDifficulty = settings.getDifficulty().equals("EASY") ? "HARD" : "EASY";
                settings.setDifficulty(newDifficulty);
                updateTaskSettings(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_save":
                messageSender.sendMessage(
                        "✅ Настройки сохранены!",
                        String.valueOf(chatId),
                        keyboardFactory.createGameSelectionKeyboard()
                );
                break;

            case "task_claim":
                claimTaskReward(chatId);
                break;

            case "task_settings":
                openTaskSettings(String.valueOf(chatId));
                break;
        }
    }

    /**
     * Показать выбор времени
     */
    private void showTimeSelectionKeyboard(Long chatId) {
        String message = "⏰ Выберите время получения заданий:";
        InlineKeyboardMarkup keyboard = createTimeSelectionKeyboard();
        messageSender.sendMessage(message, String.valueOf(chatId), keyboard);
    }

    /**
     * Создать клавиатуру выбора времени
     */
    private InlineKeyboardMarkup createTimeSelectionKeyboard() {
        return keyboardFactory.createTimeSelectionKeyboard();
    }

    /**
     * Обработка ввода времени
     */
    public void handleTimeInput(Long chatId, String timeText) {
        try {
            String[] parts = timeText.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);

                if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60) {
                    TaskSettings settings = getOrCreateTaskSettings(chatId);
                    settings.setNotificationHour(hour);
                    settings.setNotificationMinute(minute);
                    updateTaskSettings(settings);

                    openTaskSettings(String.valueOf(chatId));
                } else {
                    messageSender.sendMessage(
                            "❌ Неверное время. Используйте ЧЧ:ММ (например 14:00)",
                            String.valueOf(chatId),
                            null
                    );
                }
            } else {
                messageSender.sendMessage(
                        "❌ Неверный формат. Используйте ЧЧ:ММ (например 14:00)",
                        String.valueOf(chatId),
                        null
                );
            }
        } catch (NumberFormatException e) {
            messageSender.sendMessage(
                    "❌ Неверный формат. Используйте ЧЧ:ММ (например 14:00)",
                    String.valueOf(chatId),
                    null
            );
        }
    }

}