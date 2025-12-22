package org.example.controler.tasks;

import org.example.controler.KeyboardBuilder;
import org.example.controler.dto.*;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис для управления ежедневными заданиями (бизнес-логика)
 */
public class TaskService {
    private final UserService userService;
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private KeyboardBuilder keyboardBuilder;

    private final Map<Long, TaskProgressStateDTO> taskStates = new ConcurrentHashMap<>();
    private final Map<Long, ActiveTaskInfoDTO> activeTaskDTOs = new ConcurrentHashMap<>();
    private final Map<Long, TaskSettingsDTO> taskSettingsMap = new ConcurrentHashMap<>();
    private final TaskGenerator taskGenerator = new TaskGenerator();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TaskService(UserService userService, MessageSender messageSender, KeyboardFactory keyboardFactory, KeyboardBuilder keyboardBuilder) {
        this.userService = userService;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
        this.keyboardBuilder = keyboardBuilder;
        startDailyTaskScheduler();
    }


    /**
     * Проверить прогресс заданий после игры
     */
    public void checkTaskProgressAfterGame(Long chatId) {
        TaskProgressStateDTO oldState = getTaskProgressStateDTO(chatId);
        TaskProgressStateDTO newState = fetchCurrentProgressStateDTO(chatId);

        ActiveTaskInfoDTO activeTaskDTO = activeTaskDTOs.get(chatId);
        if (activeTaskDTO != null && !activeTaskDTO.completed) {
            int progress = calculateProgressForTask(activeTaskDTO.taskType, oldState, newState);

            if (progress > 0) {
                activeTaskDTO.currentValue += progress;
                activeTaskDTO.progressPercentage = calculateProgressPercentage(
                        activeTaskDTO.currentValue,
                        activeTaskDTO.targetValue
                );

                if (activeTaskDTO.currentValue >= activeTaskDTO.targetValue) {
                    activeTaskDTO.completed = true;
                    sendTaskCompletedNotification(chatId, activeTaskDTO);
                }
            }
        }

        taskStates.put(chatId, newState);
    }

    /**
     * Забрать награду за задание
     */
    public void claimTaskReward(Long chatId) {
        ActiveTaskInfoDTO taskDTO = activeTaskDTOs.get(chatId);

        if (taskDTO == null || !taskDTO.completed) {
            messageSender.sendMessage(
                    "❌ У вас нет выполненных заданий для получения награды",
                    String.valueOf(chatId),
                    null
            );
            return;
        }

        boolean success = userService.payWinnings(chatId, taskDTO.reward);

        if (success) {
            int newBalance = userService.getUserBalance(chatId);
            userService.changeEarned(chatId, taskDTO.reward);

            String message = "🎉 *Награда получена!*\n\n" +
                    "💰 +" + taskDTO.reward + " 🪙\n" +
                    "💎 Новый баланс: " + newBalance + " 🪙\n\n" +
                    "Задание будет обновлено завтра!";

            activeTaskDTOs.remove(chatId);
            messageSender.sendMessage(message, String.valueOf(chatId),
                    keyboardFactory.createKeyboard(keyboardBuilder.createGameSelectionButtons()));
        } else {
            messageSender.sendMessage(
                    "❌ Ошибка при получении награды",
                    String.valueOf(chatId),
                    null
            );
        }
    }


    /**
     * Получить или создать настройки DTO
     */
    public TaskSettingsDTO getOrCreateTaskSettingsDTO(Long chatId) {
        return taskSettingsMap.computeIfAbsent(chatId, id -> new TaskSettingsDTO(
                chatId,
                true,
                "14:00:00",
                "EASY",
                "ВКЛЮЧЕНЫ",
                "ЛЕГКИЙ"
        ));
    }

    /**
     * Обновить настройки
     */
    public void updateTaskSettingsDTO(TaskSettingsDTO settings) {
        taskSettingsMap.put(settings.chatId, settings);
    }

    /**
     * Обновить статус включения заданий
     */
    public void toggleTaskStatus(Long chatId) {
        TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);
        settings.enabled = !settings.enabled;
        settings.statusInRussian = settings.enabled ? "ВКЛЮЧЕНЫ" : "ВЫКЛЮЧЕНЫ";
        updateTaskSettingsDTO(settings);
    }

    /**
     * Обновить время уведомлений
     */
    public void updateNotificationTime(Long chatId, String time) {
        TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);
        settings.notificationTime = time;
        updateTaskSettingsDTO(settings);
    }

    /**
     * Переключить сложность заданий
     */
    public void toggleDifficulty(Long chatId) {
        TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);
        if (settings.difficulty.equals("EASY")) {
            settings.difficulty = "HARD";
            settings.difficultyInRussian = "СЛОЖНЫЙ";
        } else {
            settings.difficulty = "EASY";
            settings.difficultyInRussian = "ЛЕГКИЙ";
        }
        updateTaskSettingsDTO(settings);
    }



    /**
     * Обработка ввода времени
     */
    public boolean handleTimeInput(Long chatId, String timeText) {
        try {

            String[] parts = timeText.split(":");
            if (parts.length == 3) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                int second = Integer.parseInt(parts[2]);

                if (isValidTime(hour, minute, second)) {
                    TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);
                    settings.notificationTime = String.format("%02d:%02d:%02d", hour, minute, second);
                    updateTaskSettingsDTO(settings);
                    return true;
                }
            }
            sendErrorMessage(chatId, "❌ Неверное время. Используйте ЧЧ:ММ:СС (например 14:00:00)");
            return false;
        } catch (NumberFormatException e) {
            sendErrorMessage(chatId, "❌ Неверный формат. Используйте ЧЧ:ММ:СС (например 14:00:00)");
            return false;
        }
    }


    /**
     * Получить состояние прогресса пользователя
     */
    private TaskProgressStateDTO getTaskProgressStateDTO(Long chatId) {
        return taskStates.computeIfAbsent(chatId, id -> fetchCurrentProgressStateDTO(id));
    }

    /**
     * Получить текущее состояние прогресса из базы данных
     */
    private TaskProgressStateDTO fetchCurrentProgressStateDTO(Long chatId) {
        return new TaskProgressStateDTO(
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
    }

    /**
     * Рассчитать прогресс для задачи на основе изменений
     */
    private int calculateProgressForTask(String taskType, TaskProgressStateDTO oldState, TaskProgressStateDTO newState) {
        switch (taskType) {
            case "WIN_BLACKJACK":
                return newState.bjWins - oldState.bjWins;
            case "PLAY_BLACKJACK":
                return (newState.bjWins - oldState.bjWins) + (newState.bjLosses - oldState.bjLosses);
            case "EARN_BLACKJACK":
                int progress = (newState.bjEarned - oldState.bjEarned) - (newState.bjLost - oldState.bjLost);
                return Math.max(0, progress);
            case "WIN_RIDE_THE_BUS":
                return newState.rtbWins - oldState.rtbWins;
            case "PLAY_RIDE_THE_BUS":
                return (newState.rtbWins - oldState.rtbWins) + (newState.rtbLosses - oldState.rtbLosses);
            case "EARN_RIDE_THE_BUS":
                int rtbProgress = (newState.rtbEarned - oldState.rtbEarned) - (newState.rtbLost - oldState.rtbLost);
                return Math.max(0, rtbProgress);
            case "EARN_ANY":
                return newState.earned - oldState.earned;
            default:
                return 0;
        }
    }

    private int calculateProgressPercentage(int currentValue, int targetValue) {
        if (targetValue == 0) return 100;
        return Math.min(100, (currentValue * 100) / targetValue);
    }

    /**
     * Отправить уведомление о выполнении задания
     */
    private void sendTaskCompletedNotification(Long chatId, ActiveTaskInfoDTO task) {
        String message = "🎉 *Задание выполнено!*\n\n" +
                "📝 " + task.description + "\n" +
                "💰 Награда: " + task.reward + " 🪙\n\n" +
                "Нажмите кнопку ниже, чтобы забрать награду!";

        messageSender.sendMessage(message, String.valueOf(chatId),createTaskClaimMarkup());
    }
    /**
     * создание разметки для клавитуры сбор наград
     */
    private KeyboardMarkup createTaskClaimMarkup(){
        List<List<ButtonData>> buttons = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("\uD83D\uDCB0 Забрать награду!", "task_claim"));
        buttons.add(row);
        return keyboardFactory.createKeyboard(buttons);
    }

    /**
     * Создать новое задание для пользователя
     */
    private void createNewTaskForUser(Long chatId) {
        TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);

        if (!settings.enabled) {
            return;
        }

        activeTaskDTOs.remove(chatId);

        ActiveTaskInfoDTO newTaskDTO = taskGenerator.generateTaskDTO(
                chatId,
                settings.difficulty,
                LocalDate.now()
        );

        if (newTaskDTO != null) {
            activeTaskDTOs.put(chatId, newTaskDTO);
            sendTaskToUser(chatId, newTaskDTO);
        }
    }

    /**
     * Отправить задание пользователю
     */
    private void sendTaskToUser(Long chatId, ActiveTaskInfoDTO taskDTO) {
        TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);
        String difficultyText = settings.difficulty.equals("EASY") ? "Легкая" : "Сложная";
        String emoji = settings.difficulty.equals("EASY") ? "🟢" : "🔴";

        String message = emoji + " *Ежедневное задание!* (Сложность: " + difficultyText + ")\n\n" +
                "📝 *Задание:* " + taskDTO.description + "\n" +
                "🎯 *Требуется:* " + taskDTO.targetValue + "\n" +
                "💰 *Награда:* " + taskDTO.reward + " 🪙";

        messageSender.sendMessage(message, String.valueOf(chatId), keyboardFactory.createKeyboard(keyboardBuilder.createGameSelectionButtons()));
    }

    /**
     * Запуск ежедневного планировщика
     */
    private void startDailyTaskScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            LocalTime now = LocalTime.now();

            for (Long chatId : getAllUsersWithTasks()) {
                TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);

                if (settings.enabled) {
                    String[] timeParts = settings.notificationTime.split(":");
                    if (timeParts.length == 3) {
                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);
                        int second = Integer.parseInt(timeParts[2]);

                        if (now.getHour() == hour && now.getMinute() == minute && now.getSecond() == second) {
                            ActiveTaskInfoDTO currentTask = activeTaskDTOs.get(chatId);
                            if (currentTask == null || !currentTask.assignedDate.equals(LocalDate.now())) {
                                createNewTaskForUser(chatId);
                            }
                        }
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Получить всех пользователей с активными заданиями
     */
    private Set<Long> getAllUsersWithTasks() {
        return new HashSet<>(taskSettingsMap.keySet());
    }

    private boolean isValidTime(int hour, int minute, int second) {
        return hour >= 0 && hour < 24 && minute >= 0 && minute < 60 && second >= 0 && second < 60;
    }

    private void sendErrorMessage(Long chatId, String message) {
        messageSender.sendMessage(message, String.valueOf(chatId), null);
    }

}