import org.example.controler.MessageHandler;

import org.example.controler.db.UserService;
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
        Long chatID = 1234L;

        String result = messageHandler.handleMessage(message, username,chatID);

        Assert.assertEquals("Вы написали: TEST", result);
    }

    /**
     * тестирует вывод баланса
     */
    @Test
    void testBalanceCommand(){
        MessageHandler messageHandler = new MessageHandler();
        UserService userService = new UserService();

        userService.getOrCreateUser(1L,"Alice");

        String responce = messageHandler.handleMessage("/balance","Alice",1L);
        Assert.assertEquals("Ваш баланс 1000",responce);
    }
}