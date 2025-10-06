import org.example.controler.MessageHandler;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

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

        String result = messageHandler.handleUpdate(message, username);

        assertEquals("Вы написали: TEST", result);
    }
}