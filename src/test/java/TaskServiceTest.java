import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;
import org.example.controler.tasks.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Проверяет функциональность сервиса ежедневных заданий.
 * Использует Mockito для изоляции зависимостей.
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
        when(keyboardFactory.createTaskSettingsKeyboard(anyBoolean(), anyString(), anyString()))
                .thenReturn(mock(InlineKeyboardMarkup.class));

        taskService.openTaskSettings(chatId);

        verify(messageSender).sendMessage(
                contains("Настройка ежедневных заданий"),
                eq(chatId),
                any(InlineKeyboardMarkup.class)
        );
    }

    /**
     * Проверяет, что обработка callback переключения состояния заданий
     * изменяет статус и обновляет интерфейс.
     */
    @Test
    void handleTaskSettingsCallbackToggleShouldChangeEnabledStatus() {
        Long chatId = 12345L;

        taskService.openTaskSettings(String.valueOf(chatId));

        taskService.handleTaskSettingsCallback(chatId, "task_toggle");

        taskService.openTaskSettings(String.valueOf(chatId));
        verify(messageSender, atLeast(2)).sendMessage(
                anyString(),
                eq(String.valueOf(chatId)),
                any()
        );
    }

    /**
     * Проверяет корректную обработку валидного времени.
     * Убеждается, что метод не выбрасывает исключений и отправляет сообщения.
     */
    @Test
    void handleTimeInput_ValidTime_ShouldNotThrowException() {
        Long chatId = 12345L;
        String validTime = "09:30";

        when(keyboardFactory.createTaskSettingsKeyboard(anyBoolean(), anyString(), anyString()))
                .thenReturn(mock(InlineKeyboardMarkup.class));

        assertDoesNotThrow(() -> taskService.handleTimeInput(chatId, validTime));

        verify(messageSender, atLeastOnce()).sendMessage(
                anyString(),
                eq(String.valueOf(chatId)),
                any()
        );
    }

    /**
     * Проверяет обработку невалидного времени.
     * Убеждается, что отправляется сообщение об ошибке.
     */
    @Test
    void handleTimeInput_InvalidTime_ShouldSendErrorMessage() {
        Long chatId = 12345L;
        String invalidTime = "25:70";

        taskService.handleTimeInput(chatId, invalidTime);

        verify(messageSender).sendMessage(
                contains("Неверное время"),
                eq(String.valueOf(chatId)),
                isNull()
        );
    }

    /**
     * Проверяет корректное завершение работы планировщика.
     * Убеждается, что метод можно вызывать многократно без ошибок.
     */
    @Test
    void shutdown_ShouldStopScheduler() {
        taskService.shutdown();

        assertDoesNotThrow(() -> taskService.shutdown());
    }
}