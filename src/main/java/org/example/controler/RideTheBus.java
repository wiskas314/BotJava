package org.example.controler;

import org.example.controler.cards.Card;
import org.example.controler.cards.Deck;



/**
 * Класс Реализующий игру в Ride The Bus
 */
public class RideTheBus {
    private Deck deck;
    private String specialCard;
    private final KeyboardFactory keyboardFactory;
    private String chatId;
    private boolean isGameOver;
    private boolean isProcessing;

    public RideTheBus() {
        keyboardFactory = new KeyboardFactory();
        specialCard = "\uD83C\uDCCF";
        isGameOver = false;
        isProcessing = false;
        deck = new Deck(4);

    }

    /**
     * Проверка на победу
     */
    private boolean checkWin(String message, Card card) {
        switch (deck.roundNumber) {
            case 1:
                boolean isRed = card.getSuit().equals("♥️") || card.getSuit().equals("♦️");
                boolean isBlack = card.getSuit().equals("♠️") || card.getSuit().equals("♣️");
                return (message.equals("red") && isRed) || (message.equals("black") && isBlack);
            case 2:
                int valeCard1 = deck.table[0].getValue();
                int valueCurrent = card.getValue();
                return ((valueCurrent >= valeCard1 && message.equals("higher")) || (valueCurrent <= valeCard1 && message.equals("lower")));
            case 3:
                int valueCard1 = deck.table[0].getValue();
                int valueCard2 = deck.table[1].getValue();
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
     * Начало игры
     */
    public void startGame(String chatId, TelegramBot bot) {
        this.chatId = chatId;
        play(bot);
    }
    /**
     * Сброс состояния игры
     */
    private void resetGame() {
        deck.roundNumber = 1;
        deck = new Deck(4);
        deck.initializeDeck();

        isGameOver = false;
        isProcessing = false; // Сброс флага обработки
    }
    /**
     * Геттер isGameOver
     */
    public boolean IsGameOver(){
        return isGameOver;
    }
    /**
     * Метод реализующий интерфейс во время игры
     */
    private void play(TelegramBot bot) {
        String roundText = "";
        switch (deck.roundNumber) {
            case 1:
                roundText = "Раунд 1 \nВыберите цвет:";
                bot.keyboard = keyboardFactory.keyboardFirstRound();
                break;

            case 2:
                roundText = "Раунд 2 \nВыберите будет ли следующая карта старшей или младшей масти:";
                bot.keyboard = keyboardFactory.createHigherLowerKeyboard();
                break;

            case 3:
                roundText = "Раунд 3 \nВыберите будет ли следующая карта внутри или вне диапазона:";
                bot.keyboard = keyboardFactory.createRangeKeyboard();
                break;

            case 4:
                roundText = "Раунд 4 \nВыберите какой масти будет следующая карта:";
                bot.keyboard = keyboardFactory.createSuitGuessKeyboard();
                break;

            case 5:
                // Игра завершена
                handleGameOver(bot, true);
                return;
        }

        if (bot.keyboard != null) {
            bot.sendMessage(roundText + "\n" + deck.getTableAsString() + " " + specialCard, chatId, bot.keyboard);
        }
    }
    /**
     * Метод для обработки выбора пользователя
     */
    public void processUserChoice(String callbackData, TelegramBot bot) {
        if (isGameOver) {
            return;
        }

        Card card = deck.dealCard();
        deck.addToTable(card);
        boolean isWin = checkWin(callbackData, card);

        if (isWin) {
            bot.sendMessage(deck.getTableAsString() + "\nПоздравляем, вы выиграли!",chatId,null);

            if (deck.roundNumber == 4) {
                handleGameOver(bot, true);
            } else {
                deck.roundNumber=deck.roundNumber+1;
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
        if (isWinner) {
            bot.sendMessage(deck.getTableAsString() + "\nВы прошли все раунды! 🎉\nХотите выбрать другую игру?",
                    chatId,
                    keyboardFactory.createGameSelectionKeyboard());
        } else {
            bot.sendMessage(deck.getTableAsString() + "\nК сожалению, вы проиграли! Хотите сыграть снова?",
                    chatId,
                    keyboardFactory.createGameSelectionKeyboard());
        }
        resetGame();
    }
}