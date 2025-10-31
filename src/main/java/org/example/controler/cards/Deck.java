package org.example.controler.cards;



import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private ArrayList<Card> deck;
    private Random random;
    private String[] suits;
    private String[] values;



    public Deck(){
        random = new Random();
        suits = new String[]{"♠️", "♥️", "♦️", "♣️"};
        values = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        initializeDeck();
    }
    /**
     * Инициализация колоды
     */
    public void initializeDeck() {
        deck = new ArrayList<>();
        for (String suit : suits) {
            for (String value : values) {
                deck.add(new Card(value, suit));
            }
        }
        shuffleDeck();
    }

    /**
     *Используем алгоритм Фишера-Йейтса (Knuth shuffle) для перемешивания колоды
     */
    private void shuffleDeck() {
        for (int i = deck.size() - 1; i > 0; i--) {
            int randomIndex = random.nextInt(i + 1);
            Card temp = deck.get(i);
            deck.set(i, deck.get(randomIndex));
            deck.set(randomIndex, temp);
        }
    }
    /**
     *Раздача карты
     */
    public Card dealCard() {
        return deck.remove(deck.size() - 1);
    }
}
