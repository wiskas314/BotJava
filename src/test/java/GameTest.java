import org.example.controler.game.BlackJack;
import org.example.controler.game.Game;
import org.example.controler.game.RideTheBus;
import org.junit.jupiter.api.Test;

import org.junit.Assert;

/**
 * тестовый класс для проверки реализации интерфейса Game
 */
public class GameTest {
    /**
     * тестирует реализацию интерфейса Game конкретными классами игр
     * проверяет что объекты успешно создаются, инициализируются с правильным состоянием
     */
    @Test
    public void testGameInterfaceImplementation() {
        Game blackjack = new BlackJack();
        Game rideTheBus = new RideTheBus();

        Assert.assertNotNull(blackjack);
        Assert.assertNotNull(rideTheBus);

        Assert.assertFalse(blackjack.getIsGameOver());
        Assert.assertFalse(rideTheBus.getIsGameOver());
    }

}
