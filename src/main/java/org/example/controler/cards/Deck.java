package org.example.controler.cards;



import java.util.ArrayList;
import java.util.Random;

public class Deck {
    private ArrayList<Card> deck;
    private Random random;
    private String[] suits;
    private String[] values;
    public int roundNumber;
    public Card[] table;

    public Deck(int n){
        random = new Random();
        suits = new String[]{"♠️", "♥️", "♦️", "♣️"};
        values = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        roundNumber = 1;
        table = new Card[n];
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
        if (roundNumber == 1 && deck.size() < 52) {
            initializeDeck();
        }

        return deck.remove(deck.size() - 1);
    }
    /**
     *Добавление карты на стол
     */
    public void addToTable(Card card) {
        for (int i = 0; i < 4; i++) {
            if (table[i] == null) {
                table[i] = card;
                break;
            }
        }
    }
    /**
     *Получение карт на столе как строку, а не как массив Card
     */
    public String getTableAsString(){
        String tableAsString = "";
        for (Card tableCard : table){
            if (tableCard == null) break;
            tableAsString += tableCard.getCard();
        }
        return tableAsString;
    }
}
