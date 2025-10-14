package org.example.controler;

/**
 * Класс, реализующий представление карты
 */
public class Card {
    private int value;
    private String suit;
    private String fullRepresentation;

    public final int ACE = 14;
    public final int KING = 13;
    public final int QUEEN = 12;
    public final int JACK = 11;
    /**
     *Конструктор класса
     */
    public Card(String value, String suit) {
        this.suit = suit;
        this.fullRepresentation = suit + value;

        switch (value) {
            case "J":
                this.value = JACK;
                break;
            case "Q":
                this.value = QUEEN;
                break;
            case "K":
                this.value = KING;
                break;
            case "A":
                this.value = ACE;
                break;
            default:
                this.value = Integer.parseInt(value);
                break;
        }
    }

    /**
     *Получить значение карты
     */
    public int getValue() {
        return value;
    }
    /**
     *Получить масть карты
     */
    public String getSuit() {
        return suit;
    }
    /**
     *Получить предлставление карты как строку
     */
    public String getCard() {
        return fullRepresentation;
    }
}
