import org.example.controler.tasks.TaskSettings;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверяет корректность работы настроек ежедневных заданий.
 */
class TaskSettingsTest {

    /**
     * Проверяет установку значений по умолчанию при создании объекта.
     * Убеждается, что все поля инициализируются корректными значениями.
     */
    @Test
    void constructor_ShouldSetDefaultValues() {
        TaskSettings settings = new TaskSettings(12345L);

        assertTrue(settings.isEnabled());
        assertEquals(14, settings.getNotificationHour());
        assertEquals(0, settings.getNotificationMinute());
        assertEquals("EASY", settings.getDifficulty());
        assertEquals("14:00", settings.getFormattedTime());
        assertEquals("ВКЛЮЧЕНЫ", settings.getStatusInRussian());
        assertEquals("ЛЕГКИЙ", settings.getDifficultyInRussian());
    }

    /**
     * Проверяет корректное обновление времени уведомлений.
     * Тестирует установку валидных значений часа и минуты.
     */
    @Test
    void setNotificationTime_ShouldAcceptValidTime() {
        TaskSettings settings = new TaskSettings(12345L);

        settings.setNotificationHour(9);
        settings.setNotificationMinute(30);

        assertEquals(9, settings.getNotificationHour());
        assertEquals(30, settings.getNotificationMinute());
        assertEquals("09:30", settings.getFormattedTime());
    }

    /**
     * Проверяет игнорирование невалидного времени.
     * Убеждается, что некорректные значения не изменяют текущие настройки.
     */
    @Test
    void setNotificationTime_ShouldIgnoreInvalidTime() {
        TaskSettings settings = new TaskSettings(12345L);
        int originalHour = settings.getNotificationHour();
        int originalMinute = settings.getNotificationMinute();

        settings.setNotificationHour(25);
        settings.setNotificationMinute(70);

        assertEquals(originalHour, settings.getNotificationHour(),
                "Неверный час не должен измениться");
        assertEquals(originalMinute, settings.getNotificationMinute(),
                "Неверная минута не должна измениться");
    }

    /**
     * Проверяет корректное обновление уровня сложности.
     * Тестирует установку допустимого значения "HARD".
     */
    @Test
    void setDifficulty_ShouldAcceptOnlyValidValues() {
        TaskSettings settings = new TaskSettings(12345L);

        settings.setDifficulty("HARD");

        assertEquals("HARD", settings.getDifficulty());
        assertEquals("СЛОЖНЫЙ", settings.getDifficultyInRussian());
    }

    /**
     * Проверяет игнорирование невалидного уровня сложности.
     * Убеждается, что некорректное значение не изменяет текущую сложность.
     */
    @Test
    void setDifficulty_ShouldIgnoreInvalidValue() {
        TaskSettings settings = new TaskSettings(12345L);
        String originalDifficulty = settings.getDifficulty();

        settings.setDifficulty("INVALID");

        assertEquals(originalDifficulty, settings.getDifficulty(),
                "Сложность не должна измениться при неверном значении");
    }

    /**
     * Проверяет логику отправки заданий при включенных настройках.
     * Тестирует условие, когда задание должно быть отправлено сегодня.
     */
    @Test
    void shouldSendTaskToday_WhenEnabledAndNotSentToday() {
        TaskSettings settings = new TaskSettings(12345L);
        settings.setEnabled(true);

        assertTrue(settings.shouldSendTaskToday(),
                "Должно отправлять задание, если включено и еще не отправляли сегодня");
    }

    /**
     * Проверяет логику отправки заданий при отключенных настройках.
     * Тестирует условие, когда задание не должно отправляться.
     */
    @Test
    void shouldSendTaskToday_WhenDisabled_ShouldReturnFalse() {
        TaskSettings settings = new TaskSettings(12345L);
        settings.setEnabled(false);

        assertFalse(settings.shouldSendTaskToday(),
                "Не должно отправлять задание, если отключено");
    }
}