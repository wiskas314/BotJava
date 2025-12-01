package org.example.controler.tasks;
import java.time.LocalDate;
import java.util.Random;

public class TaskGenerator {
    private final Random random = new Random();

    /**
     * выбор задач
     */
    public ActiveTaskInfo generateTask(Long chatId, String difficulty, LocalDate date) {
        boolean isHard = "HARD".equals(difficulty);

        int taskIndex = random.nextInt(6);

        switch (taskIndex) {
            case 0:
                return generateBlackjackWinTask(chatId, isHard, date);
            case 1:
                return generateBlackjackPlayTask(chatId, isHard, date);
            case 2:
                return generateBlackjackEarnTask(chatId, isHard, date);
            case 3:
                return generateRideTheBusWinTask(chatId, isHard, date);
            case 4:
                return generateRideTheBusPlayTask(chatId, isHard, date);
            case 5:
                return generateEarnAnyTask(chatId, isHard, date);
            default:
                return generateBlackjackWinTask(chatId, isHard, date);
        }
    }

    /**
     *генерирует задачу на победы в bj
     */
    private ActiveTaskInfo generateBlackjackWinTask(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 3 : 2;
        int reward = isHard ? 150 : 75;

        return new ActiveTaskInfo(
                chatId,
                "WIN_BLACKJACK",
                "Выиграть " + target + " игры в Blackjack",
                isHard ? "HARD" : "EASY",
                reward,
                target,
                date
        );
    }

    /**
     *генерирует задачу в которой нужно сыграть в bj
     */
    private ActiveTaskInfo generateBlackjackPlayTask(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 5 : 3;
        int reward = isHard ? 120 : 60;

        return new ActiveTaskInfo(
                chatId,
                "PLAY_BLACKJACK",
                "Сыграть " + target + " игр в Blackjack",
                isHard ? "HARD" : "EASY",
                reward,
                target,
                date
        );
    }

    /**
     *генерирует задачу в которой нужно заработать в bj
     */
    private ActiveTaskInfo generateBlackjackEarnTask(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 500 : 250;
        int reward = isHard ? 200 : 100;

        return new ActiveTaskInfo(
                chatId,
                "EARN_BLACKJACK",
                "Заработать " + target + " кредитов в Blackjack",
                isHard ? "HARD" : "EASY",
                reward,
                target,
                date
        );
    }

    /**
     *генерирует задачу в которой нужно выиграть в rtb
     */
    private ActiveTaskInfo generateRideTheBusWinTask(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 2 : 1;
        int reward = isHard ? 180 : 90;

        return new ActiveTaskInfo(
                chatId,
                "WIN_RIDE_THE_BUS",
                "Выиграть " + target + " игры в Ride the Bus",
                isHard ? "HARD" : "EASY",
                reward,
                target,
                date
        );
    }

    /**
     * генерирует задачу сыграть в rtb
     */
    private ActiveTaskInfo generateRideTheBusPlayTask(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 4 : 2;
        int reward = isHard ? 140 : 70;

        return new ActiveTaskInfo(
                chatId,
                "PLAY_RIDE_THE_BUS",
                "Сыграть " + target + " игр в Ride the Bus",
                isHard ? "HARD" : "EASY",
                reward,
                target,
                date
        );
    }

    /**
     *генерирует задачу на заработок
     */
    private ActiveTaskInfo generateEarnAnyTask(Long chatId, boolean isHard, LocalDate date) {
        int target = isHard ? 750 : 400;
        int reward = isHard ? 250 : 125;

        return new ActiveTaskInfo(
                chatId,
                "EARN_ANY",
                "Заработать " + target + " кредитов в любой игре",
                isHard ? "HARD" : "EASY",
                reward,
                target,
                date
        );
    }
}
