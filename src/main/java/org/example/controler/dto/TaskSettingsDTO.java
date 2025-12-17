package org.example.controler.dto;

public class TaskSettingsDTO {
    public Long chatId;
    public boolean enabled;
    public String notificationTime;
    public String difficulty;
    public String statusInRussian;
    public String difficultyInRussian;

    public TaskSettingsDTO(Long chatId, boolean enabled, String notificationTime,
                           String difficulty, String statusInRussian, String difficultyInRussian) {
        this.chatId = chatId;
        this.enabled = enabled;
        this.notificationTime = notificationTime;
        this.difficulty = difficulty;
        this.statusInRussian = statusInRussian;
        this.difficultyInRussian = difficultyInRussian;
    }
}