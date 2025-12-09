import org.example.controler.tasks.ActiveTaskInfo;
import org.example.controler.tasks.TaskGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;

/**
 * Проверяет корректность генерации ежедневных заданий.
 */
class TaskGeneratorTest {

    private final TaskGenerator taskGenerator = new TaskGenerator();

    /**
     * Проверяет, что сгенерированное задание содержит все обязательные поля
     * с корректными значениями.
     */
    @Test
    void generateTask_ShouldCreateTaskWithValidFields() {
        Long chatId = 12345L;
        LocalDate date = LocalDate.now();

        ActiveTaskInfo task = taskGenerator.generateTask(chatId, "EASY", date);

        Assertions.assertNotNull(task);
        Assertions.assertEquals(chatId, task.getChatId());
        Assertions.assertEquals(date, task.getAssignedDate());
        Assertions.assertTrue(task.getReward() > 0);
        Assertions.assertTrue(task.getTargetValue() > 0);
        Assertions.assertNotNull(task.getDescription());
    }

    /**
     * Проверяет, что при многократном вызове генератора
     * создаются задания разных типов (не только одно и то же).
     * Это гарантирует разнообразие ежедневных заданий.
     */
    @Test
    void generateTask_ShouldReturnDifferentTasksOnMultipleCalls() {
        Long chatId = 12345L;
        LocalDate date = LocalDate.now();
        String[] generatedDescriptions = new String[10];

        for (int i = 0; i < 10; i++) {
            ActiveTaskInfo task = taskGenerator.generateTask(chatId, "EASY", date);
            generatedDescriptions[i] = task.getDescription();
        }

        long uniqueDescriptions = java.util.Arrays.stream(generatedDescriptions)
                .distinct()
                .count();

        Assertions.assertTrue(uniqueDescriptions >= 2,
                "Должны генерироваться разные типы заданий");
    }
}