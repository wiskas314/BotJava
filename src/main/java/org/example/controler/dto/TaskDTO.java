package org.example.controler.dto;

import java.time.LocalDate;

public class TaskDTO {
    public Long chatId;
    public String taskType;
    public String description;
    public String difficulty;
    public int reward;
    public int targetValue;
    public LocalDate assignedDate;
    public int currentValue;
    public boolean completed;
    public int progressPercentage;

    public TaskDTO(Long chatId, String taskType, String description, String difficulty,
                   int reward, int targetValue, LocalDate assignedDate,
                   int currentValue, boolean completed, int progressPercentage) {
        this.chatId = chatId;
        this.taskType = taskType;
        this.description = description;
        this.difficulty = difficulty;
        this.reward = reward;
        this.targetValue = targetValue;
        this.assignedDate = assignedDate;
        this.currentValue = currentValue;
        this.completed = completed;
        this.progressPercentage = progressPercentage;
    }
}