package org.example.controler.dto;

public class TaskNotificationDTO {
    public final Long chatId;
    public final String message;
    public final int reward;
    public final String taskType;

    public TaskNotificationDTO(Long chatId, String message, int reward, String taskType) {
        this.chatId = chatId;
        this.message = message;
        this.reward = reward;
        this.taskType = taskType;
    }
}
