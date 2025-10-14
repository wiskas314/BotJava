package org.example.controler;

public class Card {
    private int value;
    private String suit;
    private String fullRepresentation;

    public final int ACE = 14;
    public final int KING = 13;
    public final int QUEEN = 12;
    public final int JACK = 11;

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


    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    public String getCard() {
        return fullRepresentation;
    }
}
