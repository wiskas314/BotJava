import org.example.controler.TelegramBot;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.example.controler.KeyboardFactory;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Тестирует обработку команды /play в TelegramBot.
 * Проверяет, что при команде /play отправляется сообщение с выбором игр.
 */
class TelegramBotPlayCommandTest {

    @Test
    void testPlayCommand() {
        TelegramBot bot = Mockito.mock(TelegramBot.class);

        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        User user = new User();
        KeyboardFactory keyboardFactory = new KeyboardFactory();

        user.setId(123L);
        user.setFirstName("TestUser");

        chat.setId(123L);
        message.setChat(chat);
        message.setFrom(user);
        message.setText("/play");
        update.setMessage(message);

        Mockito.doAnswer(invocation -> {
            bot.sendMessage("Выберите игру", "123", keyboardFactory.createGameSelectionKeyboard());
            return null;
        }).when(bot).onUpdateReceived(update);

        Mockito.doCallRealMethod().when(bot).sendMessage(Mockito.any(), Mockito.any(), Mockito.any());

        bot.onUpdateReceived(update);

        Mockito.verify(bot).sendMessage("Выберите игру", "123", keyboardFactory.createGameSelectionKeyboard());
    }
}