package org.example.controler.cards;

/**
 * Класс, реализующий представление карты
 */
public class Card {
    private int value;
    private String suit;
    private String fullRepresentation;

    public static final int ACE = 14;
    public static final int KING = 13;
    public static final int QUEEN = 12;
    public static final int JACK = 11;
    /**
     * Конструктор класса
     */
    public Card(String value, String suit) {
        this.suit = suit;
        this.fullRepresentation = suit + value;

        this.value = switch (value){
            case "J" -> JACK;
            case "Q" -> QUEEN;
            case "K" -> KING;
            case "A" -> ACE;
            default -> Integer.parseInt(value);
        };
    }

    /**
     * Получить значение карты
     */
    public int getValue() {
        return value;
    }
    /**
     * Получить масть карты
     */
    public String getSuit() {
        return suit;
    }
    /**
     * Получить предлставление карты как строку
     */
    public String getCard() {
        return fullRepresentation;
    }
}
