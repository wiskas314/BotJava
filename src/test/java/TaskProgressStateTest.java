import org.example.controler.tasks.TaskProgressState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals(bjWins, state.getBjWins());
        assertEquals(bjLosses, state.getBjLosses());
        assertEquals(bjEarned, state.getBjEarned());
        assertEquals(bjLost, state.getBjLost());
        assertEquals(rtbWins, state.getRtbWins());
        assertEquals(rtbLosses, state.getRtbLosses());
        assertEquals(rtbEarned, state.getRtbEarned());
        assertEquals(rtbLost, state.getRtbLost());
        assertEquals(balance, state.getBalance());
        assertEquals(earned, state.getEarned());
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

        assertEquals(10, state.getBjWins());
        assertEquals(5, state.getBjLosses());
        assertEquals(2000, state.getBjEarned());
        assertEquals(1000, state.getBjLost());
        assertEquals(7, state.getRtbWins());
        assertEquals(3, state.getRtbLosses());
        assertEquals(1500, state.getRtbEarned());
        assertEquals(500, state.getRtbLost());
        assertEquals(3000, state.getBalance());
        assertEquals(2500, state.getEarned());
    }
}