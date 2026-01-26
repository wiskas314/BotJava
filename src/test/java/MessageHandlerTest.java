import org.example.controler.BalanceService;
import org.example.controler.MessageSender;
import org.example.controler.keyboard.ButtonData;
import org.example.controler.keyboard.KeyboardMarkup;
import org.example.controler.dto.MessageData;
import org.example.controler.handlers.MessageHandler;
import org.example.controler.handlers.TaskCallBackHandler;
import org.example.controler.tasks.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Тест обработки классом текста сообщения, отправленного пользователем
 */
class MessageHandlerTest {
    private TestMessageSender messageSender;
    private KeyboardMarkup keyboardFactory;
    private MessageHandler messageHandler;
    private TaskService taskService;
    private BalanceService balanceService;
    private TaskCallBackHandler taskCallBackHandler;

    @BeforeEach
    void setUp() {
        messageSender = new TestMessageSender();
        keyboardFactory = new KeyboardMarkup();
        messageHandler = new MessageHandler(messageSender, keyboardFactory,taskService,balanceService,taskCallBackHandler);
    }
    /**
     * Тестовая реализация MessageSender для проверки отправки сообщений
     */
    private class TestMessageSender implements MessageSender {
        private String lastMessage;
        private String lastChatId;
        private KeyboardMarkup lastKeyboard;

        @Override
        public void sendMessage(String text, String chatId, KeyboardMarkup keyboard) {
            this.lastMessage = text;
            this.lastChatId = chatId;
            this.lastKeyboard = keyboard;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public String getLastChatId() {
            return lastChatId;
        }

        public KeyboardMarkup getLastKeyboard() {
            return lastKeyboard;
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

        Assertions.assertEquals("Выберите игру:", messageSender.getLastMessage());
        Assertions.assertNotNull(messageSender.getLastKeyboard());

        Assertions.assertEquals(chatId, messageSender.lastChatId);
        KeyboardMarkup keyboard = messageSender.lastKeyboard;

        List<List<ButtonData>> rows = keyboard.keyboard;
        Assertions.assertEquals(1, rows.size());

        List<ButtonData> buttons = rows.get(0);
        Assertions.assertEquals(2, buttons.size());

        Assertions.assertEquals("🎮 Ride the Bus",buttons.get(0).getText());
        Assertions.assertEquals("ride_the_bus",buttons.get(0).getCallbackData());

        Assertions.assertEquals("🎮 Black Jack",buttons.get(1).getText());
        Assertions.assertEquals("black_jack",buttons.get(1).getCallbackData());
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
        String expectedHelpText = """
            Вот список доступных команд:
            /start - Начать общение с ботом
            /help - Получить список команд
            /play - Вызывает меню с выбором игр
            /balance - Показывает баланс пользователя
            /statistic - Показывает статистику пользователя
            
            """;
        Assertions.assertEquals(helpMessage,expectedHelpText);
        Assertions.assertNull(messageSender.lastKeyboard);
    }
}