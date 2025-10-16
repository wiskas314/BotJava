import org.example.game.Card;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Тест для проверки корректности работы класса Card
 */
class CardTest {

    /**
     * Тестирует создание карты с числовым значением
     */
    @Test
    void testNumericCardCreation() {
        Card card = new Card("5", "♠️");

        Assert.assertEquals(5, card.getValue());
        Assert.assertEquals("♠️", card.getSuit());
        Assert.assertEquals("♠️5", card.getCard());
    }

    /**
     * Тестирует создание карты Валет
     */
    @Test
    void testJackCardCreation() {
        Card card = new Card("J", "♦️");

        Assert.assertEquals(11, card.getValue());
        Assert.assertEquals("♦️", card.getSuit());
        Assert.assertEquals("♦️J", card.getCard());
    }

    /**
     * Тестирует создание карты Дама
     */
    @Test
    void testQueenCardCreation() {
        Card card = new Card("Q", "♥️");

        Assert.assertEquals(12, card.getValue());
        Assert.assertEquals("♥️", card.getSuit());
        Assert.assertEquals("♥️Q", card.getCard());
    }

    /**
     * Тестирует создание карты Король
     */
    @Test
    void testKingCardCreation() {
        Card card = new Card("K", "♣️");

        Assert.assertEquals(13, card.getValue());
        Assert.assertEquals("♣️", card.getSuit());
        Assert.assertEquals("♣️K", card.getCard());
    }

    /**
     * Тестирует создание карты Туз
     */
    @Test
    void testAceCardCreation() {
        Card card = new Card("A", "♠️");

        Assert.assertEquals(14, card.getValue());
        Assert.assertEquals("♠️", card.getSuit());
        Assert.assertEquals("♠️A", card.getCard());
    }
}