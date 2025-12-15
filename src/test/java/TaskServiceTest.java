import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;
import org.example.controler.tasks.TaskService;
import org.example.controler.tasks.dto.ActiveTaskInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import  org.junit.jupiter.api.Assertions;
import java.time.LocalTime;

/**
 * Проверяет функциональность сервиса ежедневных заданий.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private MessageSender messageSender;

    @Mock
    private KeyboardFactory keyboardFactory;

    private TaskService taskService;

    /**
     * Инициализация тестового окружения перед каждым тестом.
     * Создает новый экземпляр TaskService с мок-зависимостями.
     */
    @BeforeEach
    void setUp() {
        taskService = new TaskService(userService, messageSender, keyboardFactory);
    }

    /**
     * Проверяет, что открытие настроек заданий отправляет сообщение
     * с информацией о текущих настройках.
     */
    @Test
    void openTaskSettingsShouldSendMessageWithSettings() {
        String chatId = "12345";
        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.openTaskSettings(chatId);

        Mockito.verify(messageSender, Mockito.times(1)).sendMessage(
                Mockito.contains("Настройка ежедневных заданий"),
                Mockito.eq(chatId),
                Mockito.eq(mockKeyboard)
        );
    }

    /**
     * Проверяет, что переключение состояния заданий сохраняется в памяти.
     */
    @Test
    void taskToggleShouldChangeEnabledStatusInMemory() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.openTaskSettings(chatId);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(false),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.handleTaskSettingsCallback(userId, "task_toggle");

        Mockito.verify(keyboardFactory, Mockito.atLeast(1)).createTaskSettingsKeyboard(
                Mockito.eq(false),
                Mockito.eq("14:00:00"),
                Mockito.eq("EASY")
        );
    }

    /**
     * Проверяет, что нажатие кнопки изменения времени показывает клавиатуру выбора времени.
     */
    @Test
    void taskChangeTimeShouldShowTimeSelectionKeyboard() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);
        InlineKeyboardMarkup timeKeyboard = Mockito.mock(InlineKeyboardMarkup.class);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.openTaskSettings(chatId);

        Mockito.when(keyboardFactory.createTimeSelectionKeyboard())
                .thenReturn(timeKeyboard);

        taskService.handleTaskSettingsCallback(userId, "task_change_time");

        Mockito.verify(messageSender).sendMessage(
                Mockito.contains("Выберите"),
                Mockito.eq(chatId),
                Mockito.eq(timeKeyboard)
        );
    }

    /**
     * Проверяет, что выбор времени из списка сохраняется в памяти.
     */
    @Test
    void taskTimeSelectionShouldSaveSelectedTime() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);


        Mockito.lenient().when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.handleTaskSettingsCallback(userId, "task_time_14");

        Mockito.verify(messageSender).sendMessage(
                Mockito.contains("• Время получения: [14:00:00]"),
                Mockito.eq(chatId),
                Mockito.eq(mockKeyboard)
        );
    }

    /**
     * Проверяет, что изменение сложности заданий сохраняется в памяти.
     */
    @Test
    void taskChangeDifficultyShouldToggleBetweenEasyAndHard() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.openTaskSettings(chatId);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("HARD")))
                .thenReturn(mockKeyboard);

        taskService.handleTaskSettingsCallback(userId, "task_change_difficulty");

        Mockito.verify(keyboardFactory).createTaskSettingsKeyboard(
                Mockito.eq(true),
                Mockito.eq("14:00:00"),
                Mockito.eq("HARD")
        );
    }

    /**
     * Проверяет, что кнопка "Назад" возвращает к настройкам заданий.
     */
    @Test
    void taskSettingsBackButtonShouldReturnToSettings() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.handleTaskSettingsCallback(userId, "task_settings");

        Mockito.verify(messageSender).sendMessage(
                Mockito.anyString(),
                Mockito.eq(chatId),
                Mockito.eq(mockKeyboard)
        );
    }

    /**
     * Проверяет, что кнопка "Сохранить" сохраняет настройки и выходит.
     */
    @Test
    void taskSaveShouldSaveSettingsAndShowConfirmation() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("14:00:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.openTaskSettings(chatId);

        taskService.handleTaskSettingsCallback(userId, "task_save");

        Mockito.verify(messageSender, Mockito.atLeast(2)).sendMessage(
                Mockito.anyString(),
                Mockito.eq(chatId),
                Mockito.any()
        );
    }

    /**
     * Проверяет, что ввод времени вручную сохраняется.
     */
    @Test
    void handleTimeInputShouldSaveManualTime() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);
        String time = "18:30:00";

        InlineKeyboardMarkup mockKeyboard = Mockito.mock(InlineKeyboardMarkup.class);

        Mockito.when(keyboardFactory.createTaskSettingsKeyboard(
                        Mockito.eq(true),
                        Mockito.eq("18:30:00"),
                        Mockito.eq("EASY")))
                .thenReturn(mockKeyboard);

        taskService.handleTimeInput(userId, time);

        Mockito.verify(messageSender).sendMessage(
                Mockito.anyString(),
                Mockito.eq(chatId),
                Mockito.eq(mockKeyboard)
        );
    }

    /**
     * Проверяет, что прогресс не увеличивается при выключенных заданиях.
     */
    @Test
    void checkTaskProgressAfterGameShouldNotIncrementWhenTasksDisabled() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        // Выключаем задания
        taskService.handleTaskSettingsCallback(userId, "task_toggle");

        // Сбрасываем счетчик вызовов messageSender
        Mockito.reset(messageSender);

        // Симулируем игру при выключенных заданиях
        taskService.checkTaskProgressAfterGame(userId);

        // Проверяем что НЕ было отправлено сообщение о прогрессе
        Mockito.verify(messageSender, Mockito.never()).sendMessage(
                Mockito.contains("прогресс"),
                Mockito.eq(chatId),
                Mockito.any()
        );
    }

    /**
     * Проверяет, что нельзя получить награду без выполнения задания.
     */
    @Test
    void shouldNotGetRewardWithoutCompletedTask() {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        // Вызываем получение награды для пользователя без заданий
        taskService.claimTaskReward(userId);

        // Проверяем, что отправлено сообщение об ошибке
        Mockito.verify(messageSender).sendMessage(
                Mockito.contains("нет выполненных заданий"),
                Mockito.eq(chatId),
                Mockito.isNull()
        );

        // Проверяем, что баланс не менялся
        Mockito.verify(userService, Mockito.never()).payWinnings(
                Mockito.anyLong(),
                Mockito.anyInt()
        );
    }

    /**
     * Проверяет полный цикл выполнения задания: создание, прогресс, завершение, получение награды.
     */
    @Test
    void shouldCompleteFullTaskCycle() throws InterruptedException {
        Long userId = 12345L;
        String chatId = String.valueOf(userId);

        // Сбрасываем моки
        Mockito.reset(messageSender);

        // 1. Сначала открываем настройки
        taskService.openTaskSettings(chatId);

        // Проверяем, что статус ВКЛЮЧЕНЫ
        Mockito.verify(messageSender).sendMessage(
                Mockito.contains("Статус: [ВКЛЮЧЕНЫ]"),
                Mockito.eq(chatId),
                Mockito.any()
        );

        // Сбрасываем для следующих проверок
        Mockito.reset(messageSender);

        // 2. Устанавливаем время на текущее + 5 секунд
        LocalTime futureTime = LocalTime.now().plusSeconds(5);
        String notificationTime = String.format("%02d:%02d:%02d",
                futureTime.getHour(),
                futureTime.getMinute(),
                futureTime.getSecond());

        // Устанавливаем время через handleTimeInput
        taskService.handleTimeInput(userId, notificationTime);

        // 3. Проверяем, что время установилось
        Mockito.verify(messageSender).sendMessage(
                Mockito.contains("Статус: [ВКЛЮЧЕНЫ]"),
                Mockito.eq(chatId),
                Mockito.any()
        );

        Mockito.reset(messageSender);

        // 4. Мокаем начальные данные пользователя
        Mockito.when(userService.getBjWins(userId)).thenReturn(0);
        Mockito.when(userService.getBjLosses(userId)).thenReturn(0);
        Mockito.when(userService.getBjEarned(userId)).thenReturn(0);
        Mockito.when(userService.getBjLost(userId)).thenReturn(0);
        Mockito.when(userService.getRtbWins(userId)).thenReturn(0);
        Mockito.when(userService.getRtbLosses(userId)).thenReturn(0);
        Mockito.when(userService.getRtbEarned(userId)).thenReturn(0);
        Mockito.when(userService.getRtbLost(userId)).thenReturn(0);
        Mockito.when(userService.getUserBalance(userId)).thenReturn(100);
        Mockito.when(userService.getUserEarned(userId)).thenReturn(0);

        // 5. Ждем чтобы планировщик сработал
        Thread.sleep(7000);

        // 6. Проверяем, что задание было создано и отправлено
        Mockito.verify(messageSender, Mockito.timeout(1000).atLeastOnce()).sendMessage(
                Mockito.argThat(message -> message.contains("Ежедневное задание")),
                Mockito.eq(chatId),
                Mockito.any()
        );

        // 7. Получаем созданное задание
        ActiveTaskInfoDTO activeTask = taskService.getActiveTaskDTO(userId);
        Assertions.assertNotNull(activeTask, "Задание должно быть создано");
        Assertions.assertEquals(userId, activeTask.chatId, "Задание должно быть для этого пользователя");

        // 8. Проверяем начальный прогресс
        Assertions.assertEquals(0, activeTask.currentValue, "Начальный прогресс должен быть 0");
        Assertions.assertFalse(activeTask.completed, "Задание не должно быть выполнено изначально");

        // 9. Симулируем игру - сначала сохраняем начальное состояние
        taskService.checkTaskProgressAfterGame(userId);

        // 10. Теперь увеличиваем победы в базе
        Mockito.when(userService.getBjWins(userId)).thenReturn(1);

        // 11. Проверяем прогресс после одной победы
        taskService.checkTaskProgressAfterGame(userId);

        // 12. Получаем обновленное задание
        activeTask = taskService.getActiveTaskDTO(userId);

        // 13. Проверяем прогресс
        System.out.println("Тип задания: " + activeTask.taskType);
        System.out.println("Цель: " + activeTask.targetValue);
        System.out.println("Текущее: " + activeTask.currentValue);
        System.out.println("Описание: " + activeTask.description);

        // 14. Если задание на победы в Blackjack и цель 1 или 2
        if (activeTask.taskType.equals("WIN_BLACKJACK") && activeTask.targetValue == 1) {
            Assertions.assertTrue(activeTask.completed, "Задание должно быть выполнено");

            // 15. Мокаем выдачу награды
            Mockito.when(userService.payWinnings(userId, activeTask.reward)).thenReturn(true);
            Mockito.when(userService.getUserBalance(userId)).thenReturn(100 + activeTask.reward);

            // Сбрасываем моки messageSender перед получением награды
            Mockito.reset(messageSender);

            // 16. Получаем награду
            taskService.claimTaskReward(userId);

            // 17. Проверяем, что награда была выдана
            Mockito.verify(userService).payWinnings(userId, activeTask.reward);
            Mockito.verify(userService).changeEarned(userId, activeTask.reward);

            // 18. Проверяем сообщение о получении награды
            Mockito.verify(messageSender).sendMessage(
                    Mockito.contains("Награда получена"),
                    Mockito.eq(chatId),
                    Mockito.any()
            );
        }
    }
}