package org.example.controler.tasks;

public class TaskProgressState {
    private final int bjWins;
    private final int bjLosses;
    private final int bjEarned;
    private final int bjLost;
    private final int rtbWins;
    private final int rtbLosses;
    private final int rtbEarned;
    private final int rtbLost;
    private final int balance;
    private final int earned;

    public TaskProgressState(int bjWins, int bjLosses, int bjEarned, int bjLost,
                             int rtbWins, int rtbLosses, int rtbEarned, int rtbLost,
                             int balance, int earned) {
        this.bjWins = bjWins;
        this.bjLosses = bjLosses;
        this.bjEarned = bjEarned;
        this.bjLost = bjLost;
        this.rtbWins = rtbWins;
        this.rtbLosses = rtbLosses;
        this.rtbEarned = rtbEarned;
        this.rtbLost = rtbLost;
        this.balance = balance;
        this.earned = earned;
    }

    // Геттеры
    public int getBjWins() { return bjWins; }
    public int getBjLosses() { return bjLosses; }
    public int getBjEarned() { return bjEarned; }
    public int getBjLost() { return bjLost; }
    public int getRtbWins() { return rtbWins; }
    public int getRtbLosses() { return rtbLosses; }
    public int getRtbEarned() { return rtbEarned; }
    public int getRtbLost() { return rtbLost; }
    public int getBalance() { return balance; }
    public int getEarned() { return earned; }
}
