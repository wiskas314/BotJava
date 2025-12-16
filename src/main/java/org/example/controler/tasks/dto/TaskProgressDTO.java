package org.example.controler.tasks.dto;

public class TaskProgressDTO {
    public String taskType;
    public String description;
    public int currentValue;
    public int targetValue;
    public int progressPercentage;
    public boolean completed;

    public TaskProgressDTO(String taskType, String description,
                           int currentValue, int targetValue, int progressPercentage, boolean completed) {
        this.taskType = taskType;
        this.description = description;
        this.currentValue = currentValue;
        this.targetValue = targetValue;
        this.progressPercentage = progressPercentage;
        this.completed = completed;
    }
}