import org.example.controler.MessageHandler;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Тест обработки классом текста сообщения, отправленного пользователем
 */
class MessageHandlerTest {
    /**
     * Тестирует функциональность эхо-команды обработчика сообщений
     */
    @Test
    void testEchoCommand() {

        MessageHandler messageHandler = new MessageHandler();

        String message = "TEST";
        String username = "Bob";

        String result = messageHandler.handleMessage(message, username);

        Assert.assertEquals("Вы написали: TEST", result);
    }
}