import org.example.controler.BalanceService;
import org.example.controler.KeyboardBuilder;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;
import org.example.controler.dto.KeyboardMarkup;
import org.example.controler.dto.MessageData;
import org.example.controler.handlers.MessageHandler;
import org.example.controler.handlers.TaskCallBackHandler;
import org.example.controler.tasks.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Тест обработки классом текста сообщения, отправленного пользователем
 */
class MessageHandlerTest {
    private TestMessageSender messageSender;
    private KeyboardFactory keyboardFactory;
    private TaskService taskService;
    private BalanceService balanceService;
    private TaskCallBackHandler taskCallBackHandler;
    private UserService userService;
    private MessageHandler messageHandler;
    private KeyboardBuilder keyboardBuilder;

    @BeforeEach
    void setUp() {
        messageSender = new TestMessageSender();
        keyboardFactory = new KeyboardFactory();
        keyboardBuilder = new KeyboardBuilder();
        userService = new UserService();
        taskService = new TaskService(userService,messageSender, keyboardFactory, keyboardBuilder);
        balanceService = new BalanceService(userService, messageSender, keyboardBuilder, keyboardFactory);
        taskCallBackHandler = new TaskCallBackHandler(taskService, messageSender, keyboardFactory);



        messageHandler = new MessageHandler(messageSender, keyboardFactory, taskService,
                balanceService, taskCallBackHandler, userService);
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
     * Тестирует обработку команды /play
     */
    @Test
    void testPlayCommand() {

        String chatId = "12345";
        String message = "/play";
        String username = "Alice";

        MessageData messageData = new MessageData(chatId, message, username);


        messageHandler.handleMessage(messageData);


        Assertions.assertNotNull(messageSender.lastMessage);
        Assertions.assertEquals(chatId, messageSender.lastChatId);
        Assertions.assertEquals("Выберите игру:", messageSender.lastMessage);
        Assertions.assertNotNull(messageSender.lastKeyboard);
    }
    /**
     * Тестирует обработку команды /help
     */
    @Test
    void testHelpCommand() {

        String chatId = "12345";
        String message = "/help";
        String username = "Alice";

        MessageData messageData = new MessageData(chatId, message, username);


        messageHandler.handleMessage(messageData);

        Assertions.assertNotNull(messageSender.lastMessage);
        Assertions.assertEquals(chatId, messageSender.lastChatId);
        String helpMessage = messageSender.lastMessage;
        Assertions.assertTrue(helpMessage.contains("/start"));
        Assertions.assertTrue(helpMessage.contains("/help"));
        Assertions.assertTrue(helpMessage.contains("/play"));
        Assertions.assertNull(messageSender.lastKeyboard);
    }
}