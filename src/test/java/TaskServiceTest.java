import org.example.controler.BalanceService;
import org.example.controler.KeyboardBuilder;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;
import org.example.controler.dto.KeyboardMarkup;
import org.example.controler.dto.TaskSettingsDTO;
import org.example.controler.handlers.MessageHandler;
import org.example.controler.handlers.TaskCallBackHandler;
import org.example.controler.tasks.TaskService;
import org.example.controler.dto.ActiveTaskInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import  org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Проверяет функциональность сервиса ежедневных заданий.
 */

class TaskServiceTest {

    private TaskService taskService;
    private UserService userService;
    private TestMessageSender messageSender;
    private KeyboardFactory keyboardFactory;
    private KeyboardBuilder keyboardBuilder;
    private static final Long TEST_CHAT_ID = 99993L;
    private static final String TEST_USERNAME = "test_user_993";


    @BeforeEach
    void setUp() {
        messageSender = new TestMessageSender();
        userService = new UserService();
        keyboardFactory = new KeyboardFactory();
        keyboardBuilder = new KeyboardBuilder();
        taskService = new TaskService(userService, messageSender, keyboardFactory, keyboardBuilder);
        userService.getOrCreateUser(TEST_CHAT_ID,TEST_USERNAME);
    }
    /**
     * Тестовая реализация MessageSender для проверки отправки сообщений
     */
    private class TestMessageSender implements MessageSender {
        String lastMessage;
        String lastChatId;
        KeyboardMarkup lastKeyboard;

        @Override
        public void sendMessage(String text, String chatId, KeyboardMarkup keyboard) {
            this.lastMessage = text;
            this.lastChatId = chatId;
            this.lastKeyboard = keyboard;
        }

        void reset() {
            lastMessage = null;
            lastChatId = null;
            lastKeyboard = null;
        }
    }
    /**
     * тест на выполнение задания при достижении цели и то что приходит уведомление о выполнение задачи
     */
    @Test
    void testTaskCompletionWhenTargetReacged() throws InterruptedException{
        ActiveTaskInfoDTO task = new ActiveTaskInfoDTO(
                TEST_CHAT_ID,
                "WIN_BLACKJACK",
                "Выиграть в Black Jack 3 раза",
                500,
                3,
                0,
                LocalDate.now(),
                false,
                0
        );
        taskService.addTestTask(task);
        taskService.handleTimeInput(TEST_CHAT_ID, String.valueOf(LocalTime.now().plusSeconds(2)));
        Thread.sleep(2000);

        taskService.checkTaskProgressAfterGame(TEST_CHAT_ID);
        userService.changeWinsAndEarnedBlackJack(TEST_CHAT_ID,0);
        taskService.checkTaskProgressAfterGame(TEST_CHAT_ID);
        userService.changeWinsAndEarnedBlackJack(TEST_CHAT_ID,0);
        taskService.checkTaskProgressAfterGame(TEST_CHAT_ID);
        userService.changeWinsAndEarnedBlackJack(TEST_CHAT_ID,0);

        taskService.checkTaskProgressAfterGame(TEST_CHAT_ID);

        String notification = messageSender.lastMessage;
        Assertions.assertTrue(notification.contains("🎉"));
        Assertions.assertTrue(notification.contains("Задание выполнено"));
        Assertions.assertTrue(notification.contains("Выиграть в Black Jack 3 раза"));
        Assertions.assertTrue(notification.contains("500"));
        Assertions.assertTrue(notification.contains("Нажмите кнопку"));
        Assertions.assertNotNull(messageSender.lastKeyboard);
        int userbalance = userService.getUserBalance(TEST_CHAT_ID)+500;
        taskService.claimTaskReward(TEST_CHAT_ID);

        Assertions.assertEquals(userbalance,userService.getUserBalance(TEST_CHAT_ID));
    }

    /**
     * тест на коректное изменение параметров у задач
     */
    @Test
    void testTaskSettings() {
        Long chatId = TEST_CHAT_ID;


        TaskSettingsDTO settings = taskService.getOrCreateTaskSettingsDTO(chatId);


        Assertions.assertTrue(settings.enabled);
        Assertions.assertEquals("14:00:00", settings.notificationTime);
        Assertions.assertEquals("EASY", settings.difficulty);
        Assertions.assertEquals("ВКЛЮЧЕНЫ", settings.statusInRussian);
        Assertions.assertEquals("ЛЕГКИЙ", settings.difficultyInRussian);


        taskService.toggleTaskStatus(chatId);
        TaskSettingsDTO afterToggle = taskService.getOrCreateTaskSettingsDTO(chatId);
        Assertions.assertFalse(afterToggle.enabled);
        Assertions.assertEquals("ВЫКЛЮЧЕНЫ", afterToggle.statusInRussian);


        taskService.toggleTaskStatus(chatId);
        TaskSettingsDTO afterSecondToggle = taskService.getOrCreateTaskSettingsDTO(chatId);
        Assertions.assertTrue(afterSecondToggle.enabled);
        Assertions.assertEquals("ВКЛЮЧЕНЫ", afterSecondToggle.statusInRussian);


        taskService.updateNotificationTime(chatId, "15:30:00");
        TaskSettingsDTO afterTimeUpdate = taskService.getOrCreateTaskSettingsDTO(chatId);
        Assertions.assertEquals("15:30:00", afterTimeUpdate.notificationTime);


        taskService.toggleDifficulty(chatId);
        TaskSettingsDTO afterDifficultyToggle = taskService.getOrCreateTaskSettingsDTO(chatId);
        Assertions.assertEquals("HARD", afterDifficultyToggle.difficulty);
        Assertions.assertEquals("СЛОЖНЫЙ", afterDifficultyToggle.difficultyInRussian);


        taskService.toggleDifficulty(chatId);
        TaskSettingsDTO afterDifficultyToggleBack = taskService.getOrCreateTaskSettingsDTO(chatId);
        Assertions.assertEquals("EASY", afterDifficultyToggleBack.difficulty);
        Assertions.assertEquals("ЛЕГКИЙ", afterDifficultyToggleBack.difficultyInRussian);

        boolean result = taskService.handleTimeInput(chatId, "16:45:30");
        Assertions.assertTrue(result);

        TaskSettingsDTO afterHandleTime = taskService.getOrCreateTaskSettingsDTO(chatId);
        Assertions.assertEquals("16:45:30", afterHandleTime.notificationTime);
    }

    /**
     *тест проверяет что нельзяа получить награду до выполнения
     */
    @Test
    void testCannotClaimRewardWithPartialCompletion() throws InterruptedException{
        ActiveTaskInfoDTO task = new ActiveTaskInfoDTO(
                TEST_CHAT_ID,
                "WIN_BLACKJACK",
                "Выиграть в Black Jack 3 раза",
                500,
                3,
                0,
                LocalDate.now(),
                false,
                0
        );
        taskService.addTestTask(task);
        taskService.handleTimeInput(TEST_CHAT_ID, String.valueOf(LocalTime.now().plusSeconds(2)));
        Thread.sleep(2000);

        taskService.checkTaskProgressAfterGame(TEST_CHAT_ID);
        userService.changeWinsAndEarnedBlackJack(TEST_CHAT_ID,0);
        taskService.checkTaskProgressAfterGame(TEST_CHAT_ID);
        userService.changeWinsAndEarnedBlackJack(TEST_CHAT_ID,0);

        taskService.checkTaskProgressAfterGame(TEST_CHAT_ID);

        int userb=userService.getUserBalance(TEST_CHAT_ID);
        taskService.claimTaskReward(TEST_CHAT_ID);

        Assertions.assertEquals(userb,userService.getUserBalance(TEST_CHAT_ID));
    }
}