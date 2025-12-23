import org.example.controler.BalanceService;
import org.example.controler.KeyboardBuilder;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.db.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class BalanceServiceTest {
    private UserService userService;
    private BalanceService balanceService;
    private KeyboardFactory keyboardFactory;
    private KeyboardBuilder keyboardBuilder;
    private static final Long TEST_CHAT_ID = 99991L;
    private static final String TEST_USERNAME = "test_user_991";

    @Mock
    private MessageSender messageSender;



    /**
     * Инициализация тестового окружения перед выполнением каждого теста.
     * Создает экземпляр UserService и тестового пользователя для последующих операций.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userService = new UserService();
        userService.getOrCreateUser(TEST_CHAT_ID, TEST_USERNAME);
        keyboardBuilder = new KeyboardBuilder();
        keyboardFactory = new KeyboardFactory();
        balanceService = new BalanceService(userService, messageSender, keyboardBuilder, keyboardFactory);
    }

    /**
     * тест проверяет корректность работы метода
     */
    @Test
    void testHandleBalanceCommand() {

        Long chatId = TEST_CHAT_ID;
        int testBalance = 1000;

        balanceService.handleBalanceCommand(chatId);


        Mockito.verify(messageSender).sendMessage(
                Mockito.eq("Ваш баланс: " + testBalance),
                Mockito.eq(String.valueOf(chatId)),
                Mockito.any()
        );
    }

}
