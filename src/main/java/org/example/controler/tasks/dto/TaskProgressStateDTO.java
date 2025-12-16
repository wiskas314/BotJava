package org.example.controler.tasks.dto;

public class TaskProgressStateDTO {
    public int bjWins;
    public int bjLosses;
    public int bjEarned;
    public int bjLost;
    public int rtbWins;
    public int rtbLosses;
    public int rtbEarned;
    public int rtbLost;
    public int balance;
    public int earned;

    public TaskProgressStateDTO(int bjWins, int bjLosses, int bjEarned, int bjLost,
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
}
