package org.example.controler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Класс Реализующий игру в Ride The Bus
 */
public class RideTheBus {
    private String[] values;
    private String[] suits;
    private Card[] table;
    private String specialCard;
    private List<Card> deck;
    private Random random;
    private final KeyboardFactory keyboardFactory;
    private int roundNumber;
    private String chatId;
    private boolean isGameOver;
    private boolean isProcessing;

    public RideTheBus() {
        keyboardFactory = new KeyboardFactory();
        values = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        suits = new String[]{"♠️", "♥️", "♦️", "♣️"};
        specialCard = "\uD83C\uDCCF";
        random = new Random();
        roundNumber = 1;
        table = new Card[4];
        isGameOver = false;
        isProcessing = false;
        initializeDeck();

    }


    /**
     * Инициализация колоды
     */
    private void initializeDeck() {
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
    private Card dealCard() {
        if (roundNumber == 1 && deck.size() < 52) {
            initializeDeck();
        }

        return deck.remove(deck.size() - 1);
    }
    /**
     *Добавление карты на стол
     */
    private void addToTable(Card card) {
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
    private String getTableAsString(){
        String tableAsString = "";
        for (Card tableCard : table){
            if (tableCard == null) break;
            tableAsString += tableCard.getCard();
            }
        return tableAsString;
    }
    /**
     *Проверка на победу
     */
    private boolean checkWin(String message, Card card) {
        switch (roundNumber) {
            case 1:
                boolean isRed = card.getSuit().equals("♥️") || card.getSuit().equals("♦️");
                boolean isBlack = card.getSuit().equals("♠️") || card.getSuit().equals("♣️");
                return (message.equals("red") && isRed) || (message.equals("black") && isBlack);
            case 2:
                int valeCard1 = table[0].getValue();
                int valueCurrent = card.getValue();
                return ((valueCurrent >= valeCard1 && message.equals("higher")) || (valueCurrent <= valeCard1 && message.equals("lower")));
            case 3:
                int valueCard1 = table[0].getValue();
                int valueCard2 = table[1].getValue();
                int currentValue = card.getValue();
                int min = Math.min(valueCard1, valueCard2);
                int max = Math.max(valueCard1, valueCard2);

                if (currentValue >= min && currentValue <= max) {
                    if (message.equals("inside")) {
                        return true;
                    }
                }
                return (message.equals("outside") && (currentValue < min | currentValue > max));
            case 4:
                String cardSuit = card.getSuit();

                return ((cardSuit.equals("♥️") && message.equals("hearts")) ||
                        (cardSuit.equals("♦️") && message.equals("diamonds")) ||
                        (cardSuit.equals("♠️") && message.equals("peacks")) ||
                        (cardSuit.equals("♣️") && message.equals("clubs")));
        }
        return true;
    }
    /**
     *Начало игры
     */
    public void startGame(String chatId, TelegramBot bot) {
        this.chatId = chatId;
        play(bot);
    }
    /**
     *Сброс состояния игры
     */
    private void resetGame() {
        roundNumber = 1;
        table = new Card[4];
        initializeDeck();
        random = new Random();
        isGameOver = false;
        isProcessing = false; // Сброс флага обработки
    }
    /**
     *Геттер isGameOver
     */
    public boolean getIsGameOver(){
        return isGameOver;
    }
    /**
     *Метод реализующий интерфейс во время игры
     */
    private void play(TelegramBot bot) {
        SendMessage message = null;

        switch (roundNumber) {
            case 1:
                message = keyboardFactory.KeyboardFirstRound(chatId);
                break;

            case 2:
                message = keyboardFactory.createHigherLowerKeyboard(chatId);
                break;

            case 3:
                message = keyboardFactory.createRangeKeyboard(chatId);
                break;

            case 4:
                message = keyboardFactory.createSuitGuessKeyboard(chatId);
                break;

            case 5:
                // Игра завершена
                handleGameOver(bot, true);
                return;
        }

        if (message != null) {
            message.setText(message.getText() + "\n" + getTableAsString() + " " + specialCard);
        }
        bot.send(message);
    }
    /**
     *Метод для обработки выбора пользователя
     */
    public void processUserChoice(String callbackData, TelegramBot bot) {
        if (isGameOver) {
            return;
        }

        Card card = dealCard();
        addToTable(card);
        boolean isWin = checkWin(callbackData, card);
        SendMessage winMessage = new SendMessage();
        winMessage.setChatId(chatId);

        if (isWin) {
            winMessage.setText(getTableAsString() + "\nПоздравляем, вы выиграли!");
            bot.send(winMessage);

            if (roundNumber == 4) {
                    handleGameOver(bot, true);
            } else {
                roundNumber=roundNumber+1;
                play(bot);
            }
        } else {
            handleGameOver(bot, false);
        }

    }
    /**
     *Обработка конца игры
     */
    private void handleGameOver(TelegramBot bot, boolean isWinner) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (isWinner) {
            message.setText(getTableAsString() + "\nВы прошли все раунды! 🎉\nХотите выбрать другую игру?");
        } else {
            message.setText(getTableAsString() + "\nК сожалению, вы проиграли! Хотите сыграть снова?");
        }

        message.setReplyMarkup(keyboardFactory.createGameSelectionKeyboard(chatId).getReplyMarkup());
        bot.send(message);
        resetGame();
    }



}