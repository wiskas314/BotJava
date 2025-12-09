package org.example.controler.tasks;

import java.time.LocalDate;

/**
 * Настройки ежедневных заданий для пользователя
 */
public class TaskSettings {
    private Long chatId;
    private boolean enabled;
    private int notificationHour;
    private int notificationMinute;
    private String difficulty;
    private LocalDate lastTaskDate;
    private int completedTasksCount;

    public TaskSettings(Long chatId) {
        this.chatId = chatId;
        this.enabled = true;
        this.notificationHour = 14;
        this.notificationMinute = 0;
        this.difficulty = "EASY";
        this.lastTaskDate = null;
        this.completedTasksCount = 0;
    }

    public Long getChatId() {
        return chatId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getNotificationHour() {
        return notificationHour;
    }

    public void setNotificationHour(int hour) {
        if (hour >= 0 && hour < 24) {
            this.notificationHour = hour;
        }
    }

    public int getNotificationMinute() {
        return notificationMinute;
    }

    public void setNotificationMinute(int minute) {
        if (minute >= 0 && minute < 60) {
            this.notificationMinute = minute;
        }
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        if (difficulty.equals("EASY") || difficulty.equals("HARD")) {
            this.difficulty = difficulty;
        }
    }


    /**
     * Получить время в формате строки (ЧЧ:ММ)
     */
    public String getFormattedTime() {
        return String.format("%02d:%02d", notificationHour, notificationMinute);
    }

    /**
     * Получить сложность на русском языке
     */
    public String getDifficultyInRussian() {
        return difficulty.equals("EASY") ? "ЛЕГКИЙ" : "СЛОЖНЫЙ";
    }

    /**
     * Получить статус на русском языке
     */
    public String getStatusInRussian() {
        return enabled ? "ВКЛЮЧЕНЫ" : "ВЫКЛЮЧЕНЫ";
    }

    /**
     * Проверить, нужно ли отправлять задание сегодня
     */
    public boolean shouldSendTaskToday() {
        if (!enabled) {
            return false;
        }
        return lastTaskDate == null || !lastTaskDate.equals(LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format(
                "TaskSettings{chatId=%d, enabled=%s, time=%s, difficulty=%s, lastTaskDate=%s, completed=%d}",
                chatId, enabled, getFormattedTime(), difficulty, lastTaskDate, completedTasksCount
        );
    }
}
