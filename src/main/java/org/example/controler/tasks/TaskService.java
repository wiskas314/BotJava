package org.example.controler.tasks;

import org.example.controler.dto.ActiveTaskInfoDTO;
import org.example.controler.dto.TaskProgressStateDTO;
import org.example.controler.dto.TaskSettingsDTO;
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

    private final Map<Long, TaskProgressStateDTO> taskStates = new ConcurrentHashMap<>();
    private final Map<Long, ActiveTaskInfoDTO> activeTaskDTOs = new ConcurrentHashMap<>();
    private final Map<Long, TaskSettingsDTO> taskSettingsMap = new ConcurrentHashMap<>();
    private final TaskGenerator taskGenerator = new TaskGenerator();

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
    private TaskProgressStateDTO getTaskProgressStateDTO(Long chatId) {
        return taskStates.computeIfAbsent(chatId, id -> {
            return new TaskProgressStateDTO(
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
                activeTaskDTO.progressPercentage = calculateProgressPercentage(activeTaskDTO.currentValue, activeTaskDTO.targetValue);

                if (activeTaskDTO.currentValue >= activeTaskDTO.targetValue) {
                    activeTaskDTO.completed = true;
                    sendTaskCompletedNotification(chatId, activeTaskDTO);
                }
            }
        }

        taskStates.put(chatId, newState);
    }

    /**
     * Рассчитать прогресс для задачи на основе изменений
     */
    private int calculateProgressForTask(String taskType, TaskProgressStateDTO oldState, TaskProgressStateDTO newState) {
        switch (taskType) {
            case "WIN_BLACKJACK":
                return newState.bjWins - oldState.bjWins;
            case "PLAY_BLACKJACK":
                return (newState.bjWins - oldState.bjWins) +
                        (newState.bjLosses - oldState.bjLosses);
            case "EARN_BLACKJACK":
                int progress = (newState.bjEarned - oldState.bjEarned) -
                        (newState.bjLost - oldState.bjLost);
                return Math.max(0, progress);
            case "WIN_RIDE_THE_BUS":
                return newState.rtbWins - oldState.rtbWins;
            case "PLAY_RIDE_THE_BUS":
                return (newState.rtbWins - oldState.rtbWins) +
                        (newState.rtbLosses - oldState.rtbLosses);
            case "EARN_RIDE_THE_BUS":
                int rtbProgress = (newState.rtbEarned - oldState.rtbEarned) -
                        (newState.rtbLost - oldState.rtbLost);
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

        InlineKeyboardMarkup keyboard = createTaskClaimKeyboard();
        messageSender.sendMessage(message, String.valueOf(chatId), keyboard);
    }

    /**
     * Открыть меню настроек заданий
     */
    public void openTaskSettings(String chatIdStr) {
        Long chatId = Long.valueOf(chatIdStr);
        TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);

        String message = "⚙️ Настройка ежедневных заданий:\n\n" +
                "• Статус: [" + settings.statusInRussian + "]\n" +
                "• Время получения: [" + settings.notificationTime + "]\n" +
                "• Уровень сложности: [" + settings.difficultyInRussian + "]";

        InlineKeyboardMarkup keyboard = createTaskSettingsKeyboard(settings);
        messageSender.sendMessage(message, chatIdStr, keyboard);
    }

    /**
     * Получить или создать настройки DTO
     */
    private TaskSettingsDTO getOrCreateTaskSettingsDTO(Long chatId) {
        return taskSettingsMap.computeIfAbsent(chatId, id -> {
            String formattedTime = "14:00:00";
            String difficulty = "EASY";
            return new TaskSettingsDTO(
                    chatId,
                    true,
                    formattedTime,
                    difficulty,
                    "ВКЛЮЧЕНЫ",
                    "ЛЕГКИЙ"
            );
        });
    }

    /**
     * Обновить настройки
     */
    private void updateTaskSettingsDTO(TaskSettingsDTO settings) {
        taskSettingsMap.put(settings.chatId, settings);
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

        InlineKeyboardMarkup keyboard = keyboardFactory.createGameSelectionKeyboard();
        messageSender.sendMessage(message, String.valueOf(chatId), keyboard);
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
                TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);

                if (settings.enabled) {
                    // Парсим время из строки
                    String[] timeParts = settings.notificationTime.split(":");
                    if (timeParts.length == 3) {
                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);

                        if (now.getHour() == hour && now.getMinute() == minute) {
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

    /**
     * Создать клавиатуру для получения награды
     */
    private InlineKeyboardMarkup createTaskClaimKeyboard() {
        return keyboardFactory.createTaskClaimKeyboard();
    }

    /**
     * Создать клавиатуру для настроек
     */
    private InlineKeyboardMarkup createTaskSettingsKeyboard(TaskSettingsDTO settings) {
        return keyboardFactory.createTaskSettingsKeyboard(
                settings.enabled,
                settings.notificationTime,
                settings.difficulty
        );
    }

    /**
     * Обработка callback от настроек
     */
    public void handleTaskSettingsCallback(Long chatId, String callbackData) {
        TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);

        switch (callbackData) {
            case "task_toggle":
                settings.enabled = !settings.enabled;
                settings.statusInRussian = settings.enabled ? "ВКЛЮЧЕНЫ" : "ВЫКЛЮЧЕНЫ";
                updateTaskSettingsDTO(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_change_time":
                showTimeSelectionKeyboard(chatId);
                break;

            case "task_time_9":
                settings.notificationTime = "09:00:00";
                updateTaskSettingsDTO(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_time_14":
                settings.notificationTime = "14:00:00";
                updateTaskSettingsDTO(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_time_20":
                settings.notificationTime = "20:00:00";
                updateTaskSettingsDTO(settings);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_change_difficulty":
                if (settings.difficulty.equals("EASY")) {
                    settings.difficulty = "HARD";
                    settings.difficultyInRussian = "СЛОЖНЫЙ";
                } else {
                    settings.difficulty = "EASY";
                    settings.difficultyInRussian = "ЛЕГКИЙ";
                }
                updateTaskSettingsDTO(settings);
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
            if (parts.length == 3) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                int second = Integer.parseInt(parts[2]);

                if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60 && second >= 0 && second < 60) {
                    TaskSettingsDTO settings = getOrCreateTaskSettingsDTO(chatId);
                    settings.notificationTime = String.format("%02d:%02d:%02d", hour, minute, second);
                    updateTaskSettingsDTO(settings);

                    openTaskSettings(String.valueOf(chatId));
                } else {
                    messageSender.sendMessage(
                            "❌ Неверное время. Используйте ЧЧ:ММ:СС (например 14:00:00)",
                            String.valueOf(chatId),
                            null
                    );
                }
            } else {
                messageSender.sendMessage(
                        "❌ Неверный формат. Используйте ЧЧ:ММ:СС (например 14:00:00)",
                        String.valueOf(chatId),
                        null
                );
            }
        } catch (NumberFormatException e) {
            messageSender.sendMessage(
                    "❌ Неверный формат. Используйте ЧЧ:ММ:СС (например 14:00:00)",
                    String.valueOf(chatId),
                    null
            );
        }
    }

    /**
     * Метод для тестов - получить активное задание пользователя
     */
    public ActiveTaskInfoDTO getActiveTaskDTOForTest(Long chatId) {
        return activeTaskDTOs.get(chatId);
    }

    public TaskSettingsDTO getTaskSettingsDTOForTest(Long testChatId) {
        return taskSettingsMap.get(testChatId);
    }

    /**
     * Получить DTO активного задания
     */
    public ActiveTaskInfoDTO getActiveTaskDTO(Long chatId) {
        return activeTaskDTOs.get(chatId);
    }
}