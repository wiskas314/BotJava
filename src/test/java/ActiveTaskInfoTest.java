import org.example.controler.tasks.ActiveTaskInfo;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяет логику работы с прогрессом выполнения заданий.
 */
class ActiveTaskInfoTest {

    /**
     * Проверяет, что задание помечается выполненным при достижении целевого значения.
     * Тестирует постепенное накопление прогресса.
     */
    @Test
    void incrementProgress_ShouldMarkTaskCompletedWhenTargetReached() {
        ActiveTaskInfo task = new ActiveTaskInfo(
                12345L,
                "WIN_BLACKJACK",
                "Выиграть 3 игры",
                "EASY",
                100,
                3,  // target
                LocalDate.now()
        );

        task.incrementProgress(2);
        assertFalse(task.isCompleted(), "Задание не должно быть выполнено после 2/3");

        task.incrementProgress(1);

        assertTrue(task.isCompleted(), "Задание должно быть выполнено после достижения цели");
    }

    /**
     * Проверяет, что задание не считается выполненным при неполном прогрессе.
     */
    @Test
    void incrementProgress_ShouldNotCompleteIfProgressLessThanTarget() {
        ActiveTaskInfo task = new ActiveTaskInfo(
                12345L,
                "WIN_BLACKJACK",
                "Выиграть 5 игр",
                "HARD",
                200,
                5,
                LocalDate.now()
        );

        task.incrementProgress(3);

        assertFalse(task.isCompleted(), "Задание не должно быть выполнено при 3/5");
    }

    /**
     * Проверяет, что задание завершается даже при превышении целевого значения.
     * Тестирует обработку "лишнего" прогресса.
     */
    @Test
    void incrementProgress_ShouldCompleteWithExtraProgress() {
        ActiveTaskInfo task = new ActiveTaskInfo(
                12345L,
                "PLAY_BLACKJACK",
                "Сыграть 2 игры",
                "EASY",
                50,
                2,
                LocalDate.now()
        );

        task.incrementProgress(5);

        assertTrue(task.isCompleted(), "Задание должно быть выполнено даже с лишним прогрессом");
    }
}