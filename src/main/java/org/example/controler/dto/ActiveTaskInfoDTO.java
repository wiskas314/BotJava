package org.example.controler.dto;

import java.time.LocalDate;

/**
 * Класс для активного задания пользователя
 * Содержит информацию о текущем задании, его прогрессе и награде
 */
public class ActiveTaskInfoDTO {
    public Long chatId;
    public String taskType;
    public String description;
    public int reward;
    public int targetValue;
    public int currentValue;
    public final LocalDate assignedDate;
    public boolean completed;
    public  int progressPercentage;

    public ActiveTaskInfoDTO(Long chatId, String taskType, String description,
                              int reward, int targetValue,
                             int currentValue, LocalDate assignedDate,
                             boolean completed, int progressPercentage) {
        this.chatId = chatId;
        this.taskType = taskType;
        this.description = description;
        this.reward = reward;
        this.targetValue = targetValue;
        this.currentValue = currentValue;
        this.assignedDate = assignedDate;
        this.completed = completed;
        this.progressPercentage = progressPercentage;
    }
}
