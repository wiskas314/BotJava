package org.example.controler.tasks;

public class TaskSettings {
    public Long chatId;
    public boolean enabled;
    public String notificationTime;
    public String difficulty;
    public String statusInRussian;
    public String difficultyInRussian;

    public TaskSettings(Long chatId, boolean enabled, String notificationTime,
                        String difficulty, String statusInRussian, String difficultyInRussian) {
        this.chatId = chatId;
        this.enabled = enabled;
        this.notificationTime = notificationTime;
        this.difficulty = difficulty;
        this.statusInRussian = statusInRussian;
        this.difficultyInRussian = difficultyInRussian;
    }
}