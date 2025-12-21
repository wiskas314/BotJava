package org.example.controler.tasks;

import org.example.controler.dto.ActiveTaskInfoDTO;
import java.time.LocalDate;
import java.util.Random;

public class TaskGenerator {
    private final Random random = new Random();

    /**
     * Выбор задач
     */
    public ActiveTaskInfoDTO generateTaskDTO(Long chatId, String difficulty, LocalDate date) {
        boolean isHard = "HARD".equals(difficulty);

        int taskIndex = random.nextInt(6);

        switch (taskIndex) {
            case 0:
                return generateBlackjackWinTaskDTO(chatId, isHard, date);
            case 1:
                return generateBlackjackPlayTaskDTO(chatId, isHard, date);
            case 2:
                return generateBlackjackEarnTaskDTO(chatId, isHard, date);
            case 3:
                return generateRideTheBusWinTaskDTO(chatId, isHard, date);
            case 4:
                return generateRideTheBusPlayTaskDTO(chatId, isHard, date);
            case 5:
                return generateEarnAnyTaskDTO(chatId, isHard, date);
            default:
                return generateBlackjackWinTaskDTO(chatId, isHard, date);
        }
    }

    /**
     * Генерирует задачу на победы в BJ
     */
    private ActiveTaskInfoDTO generateBlackjackWinTaskDTO(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 3 : 2;
        int reward = isHard ? 150 : 75;

        return new ActiveTaskInfoDTO(
                chatId,
                "WIN_BLACKJACK",
                "Выиграть " + target + " игры в Blackjack",
                reward,
                target,
                0, // currentValue
                date,
                false, // completed
                0 // progressPercentage
        );
    }

    /**
     * Генерирует задачу в которой нужно сыграть в BJ
     */
    private ActiveTaskInfoDTO generateBlackjackPlayTaskDTO(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 5 : 3;
        int reward = isHard ? 120 : 60;

        return new ActiveTaskInfoDTO(
                chatId,
                "PLAY_BLACKJACK",
                "Сыграть " + target + " игр в Blackjack",
                reward,
                target,
                0,
                date,
                false,
                0
        );
    }

    /**
     * Генерирует задачу в которой нужно заработать в BJ
     */
    private ActiveTaskInfoDTO generateBlackjackEarnTaskDTO(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 500 : 250;
        int reward = isHard ? 200 : 100;

        return new ActiveTaskInfoDTO(
                chatId,
                "EARN_BLACKJACK",
                "Заработать " + target + " кредитов в Blackjack",
                reward,
                target,
                0,
                date,
                false,
                0
        );
    }

    /**
     * Генерирует задачу в которой нужно выиграть в RTB
     */
    private ActiveTaskInfoDTO generateRideTheBusWinTaskDTO(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 2 : 1;
        int reward = isHard ? 180 : 90;

        return new ActiveTaskInfoDTO(
                chatId,
                "WIN_RIDE_THE_BUS",
                "Выиграть " + target + " игры в Ride the Bus",
                reward,
                target,
                0,
                date,
                false,
                0
        );
    }

    /**
     * Генерирует задачу сыграть в RTB
     */
    private ActiveTaskInfoDTO generateRideTheBusPlayTaskDTO(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 4 : 2;
        int reward = isHard ? 140 : 70;

        return new ActiveTaskInfoDTO(
                chatId,
                "PLAY_RIDE_THE_BUS",
                "Сыграть " + target + " игр в Ride the Bus",
                reward,
                target,
                0,
                date,
                false,
                0
        );
    }

    /**
     * Генерирует задачу на заработок
     */
    private ActiveTaskInfoDTO generateEarnAnyTaskDTO(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 750 : 400;
        int reward = isHard ? 250 : 125;

        return new ActiveTaskInfoDTO(
                chatId,
                "EARN_ANY",
                "Заработать " + target + " кредитов в любой игре",
                reward,
                target,
                0,
                date,
                false,
                0
        );
    }
}