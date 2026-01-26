package org.example.controler.handlers;

import org.example.controler.MessageSender;
import org.example.controler.keyboard.ButtonData;
import org.example.controler.keyboard.KeyboardMarkup;
import org.example.controler.tasks.TaskSettings;
import org.example.controler.tasks.TaskService;


import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик callback для заданий (UI-логика)
 */
public class TaskCallBackHandler {
    private final TaskService taskService;
    private final MessageSender messageSender;
    private final KeyboardMarkup keyboardFactory;

    public TaskCallBackHandler(TaskService taskService, MessageSender messageSender,
                               KeyboardMarkup keyboardFactory) {
        this.taskService = taskService;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;


    }

    /**
     * Обработка callback от настроек заданий
     */
    public void handleTaskSettingsCallback(Long chatId, String callbackData) {
        System.out.println("Мы обрабатываем таск колбэк");
        switch (callbackData) {
            case "task_toggle":
                taskService.toggleTaskStatus(chatId);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_change_time":
                showTimeSelectionKeyboard(chatId);
                break;

            case "task_time_9":
                taskService.updateNotificationTime(chatId, "09:00:00");
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_time_14":
                taskService.updateNotificationTime(chatId, "14:00:00");
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_time_20":
                taskService.updateNotificationTime(chatId, "20:00:00");
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_change_difficulty":
                taskService.toggleDifficulty(chatId);
                openTaskSettings(String.valueOf(chatId));
                break;

            case "task_claim":
                taskService.claimTaskReward(chatId);
                break;

            case "task_settings":
                openTaskSettings(String.valueOf(chatId));
                break;
        }
    }

    /**
     * Открыть меню настроек заданий
     */
    public void openTaskSettings(String chatIdStr) {
        Long chatId = Long.valueOf(chatIdStr);
        TaskSettings settings = taskService.getOrCreateTaskSettingsDTO(chatId);

        String message = "⚙️ Настройка ежедневных заданий:\n\n" +
                "• Статус: [" + settings.statusInRussian + "]\n" +
                "• Время получения: [" + settings.notificationTime + "]\n" +
                "• Уровень сложности: [" + settings.difficultyInRussian + "]";

        KeyboardMarkup keyboard = createMarkup(
                settings.enabled,
                settings.notificationTime,
                settings.difficulty
        );
        messageSender.sendMessage(message, chatIdStr, keyboard);
    }
    /**
     * метод создающий разметку для клавиатуры с настройками
     */
    private KeyboardMarkup createMarkup(boolean enabled,String time,String difficulty){
        String toggleText = enabled ? "❌ Выключить" : "✅ Включить";
        String difficultyText = difficulty.equals("EASY") ? "СЛОЖНЫЙ" : "ЛЕГКИЙ";

        List<List<ButtonData>> buttons = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData(toggleText, "task_toggle"));
        row.add(new ButtonData("⏰ " + time, "task_change_time"));
        buttons.add(row);

        List<ButtonData> row2 = new ArrayList<>();
        row2.add(new ButtonData(difficulty, "task_change_difficulty"));
        row2.add(new ButtonData("→ " + difficultyText, "task_change_difficulty"));
        buttons.add(row2);

        return keyboardFactory.createKeyboard(buttons);
    }

    /**
     * Показать выбор времени
     */
    private void showTimeSelectionKeyboard(Long chatId) {
        String message = "⏰ Выберите время получения заданий:";
        List<List<ButtonData>> buttons = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("09:00", "task_time_9"));
        row.add(new ButtonData("14:00", "task_time_14"));
        buttons.add(row);

        List<ButtonData> row2 = new ArrayList<>();
        row2.add(new ButtonData("20:00", "task_time_20"));
        row2.add(new ButtonData("↩\uFE0F Назад", "task_settings"));
        buttons.add(row2);
        KeyboardMarkup keyboard = keyboardFactory.createKeyboard(buttons);
        messageSender.sendMessage(message, String.valueOf(chatId), keyboard);
    }
}
