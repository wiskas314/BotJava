import org.example.controler.tasks.TaskProgressState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Проверяет корректность хранения и получения статистики прогресса.
 */
class TaskProgressStateTest {

    /**
     * Проверяет корректное сохранение всех значений в конструкторе.
     * Тестирует инициализацию полей статистики по играм.
     */
    @Test
    void constructor_ShouldStoreAllValues() {
        int bjWins = 5;
        int bjLosses = 3;
        int bjEarned = 1000;
        int bjLost = 500;
        int rtbWins = 2;
        int rtbLosses = 4;
        int rtbEarned = 800;
        int rtbLost = 300;
        int balance = 2000;
        int earned = 1800;

        TaskProgressState state = new TaskProgressState(
                bjWins, bjLosses, bjEarned, bjLost,
                rtbWins, rtbLosses, rtbEarned, rtbLost,
                balance, earned
        );

        Assertions.assertEquals(bjWins, state.getBjWins());
        Assertions.assertEquals(bjLosses, state.getBjLosses());
        Assertions.assertEquals(bjEarned, state.getBjEarned());
        Assertions.assertEquals(bjLost, state.getBjLost());
        Assertions.assertEquals(rtbWins, state.getRtbWins());
        Assertions.assertEquals(rtbLosses, state.getRtbLosses());
        Assertions.assertEquals(rtbEarned, state.getRtbEarned());
        Assertions.assertEquals(rtbLost, state.getRtbLost());
        Assertions.assertEquals(balance, state.getBalance());
        Assertions.assertEquals(earned, state.getEarned());
    }

    /**
     * Проверяет корректность работы геттеров.
     * Тестирует получение сохраненных значений статистики.
     */
    @Test
    void getters_ShouldReturnCorrectValues() {
        TaskProgressState state = new TaskProgressState(
                10, 5, 2000, 1000,
                7, 3, 1500, 500,
                3000, 2500
        );

        Assertions.assertEquals(10, state.getBjWins());
        Assertions.assertEquals(5, state.getBjLosses());
        Assertions.assertEquals(2000, state.getBjEarned());
        Assertions.assertEquals(1000, state.getBjLost());
        Assertions.assertEquals(7, state.getRtbWins());
        Assertions.assertEquals(3, state.getRtbLosses());
        Assertions.assertEquals(1500, state.getRtbEarned());
        Assertions.assertEquals(500, state.getRtbLost());
        Assertions.assertEquals(3000, state.getBalance());
        Assertions.assertEquals(2500, state.getEarned());
    }
}