package org.example.controler.tasks;
import java.time.LocalDate;

public class ActiveTaskInfo {
    private final Long chatId;
    private final String taskType;
    private final String description;
    private final String difficulty;
    private final int reward;
    private final int targetValue;
    private int currentValue;
    private final LocalDate assignedDate;
    private boolean completed;

    public ActiveTaskInfo(Long chatId, String taskType, String description,
                          String difficulty, int reward, int targetValue, LocalDate assignedDate) {
        this.chatId = chatId;
        this.taskType = taskType;
        this.description = description;
        this.difficulty = difficulty;
        this.reward = reward;
        this.targetValue = targetValue;
        this.assignedDate = assignedDate;
        this.currentValue = 0;
        this.completed = false;
    }

    public Long getChatId() { return chatId; }
    public String getTaskType() { return taskType; }
    public String getDescription() { return description; }
    public int getReward() { return reward; }
    public int getTargetValue() { return targetValue; }
    public LocalDate getAssignedDate() { return assignedDate; }
    public boolean isCompleted() { return completed; }

    /**
     * Увеличить прогресс
     */
    public void incrementProgress(int amount) {
        this.currentValue += amount;
        if (this.currentValue >= this.targetValue) {
            this.completed = true;
        }
    }
}
