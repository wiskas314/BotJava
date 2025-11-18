package org.example.controler;

import org.example.controler.cards.Card;
import org.example.controler.cards.Deck;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

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
    private GameCallBack gameCallback;

    public RideTheBus() {
        keyboardFactory = new KeyboardFactory();
        specialCard = "\uD83C\uDCCF";
        isGameOver = false;
        isProcessing = false;
        deck = new Deck(4);
    }

    /**
     * Устанавливает callback для взаимодействия с внешним миром
     */
    public void setGameCallback(GameCallBack callback) {
        this.gameCallback = callback;
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
    public void startGame(String chatId) {
        this.chatId = chatId;
        play();
    }

    /**
     * Сброс состояния игры
     */
    private void resetGame() {
        deck.roundNumber = 1;
        deck = new Deck(4);
        deck.initializeDeck();

        isGameOver = false;
        isProcessing = false;
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
    private void play() {
        if (gameCallback == null || chatId == null) {
            System.err.println("GameCallback or chatId is null in play()");
            return;
        }

        String roundText = "";
        InlineKeyboardMarkup keyboard = null;

        switch (deck.roundNumber) {
            case 1:
                roundText = "Раунд 1 \nВыберите цвет:";
                keyboard = keyboardFactory.keyboardFirstRound();
                break;

            case 2:
                roundText = "Раунд 2 \nВыберите будет ли следующая карта старшей или младшей масти:";
                keyboard = keyboardFactory.createHigherLowerKeyboard();
                break;

            case 3:
                roundText = "Раунд 3 \nВыберите будет ли следующая карта внутри или вне диапазона:";
                keyboard = keyboardFactory.createRangeKeyboard();
                break;

            case 4:
                roundText = "Раунд 4 \nВыберите какой масти будет следующая карта:";
                keyboard = keyboardFactory.createSuitGuessKeyboard();
                break;

            case 5:
                handleGameOver(true);
                return;
        }

        if (keyboard != null) {
            gameCallback.sendGameMessage(chatId,roundText + "\n" + deck.getTableAsString() + " " + specialCard, keyboard);
        }
    }

    /**
     * Метод для обработки выбора пользователя
     */
    public void processUserChoice(String callbackData) {
        if (isGameOver || gameCallback == null) {
            return;
        }

        Card card = deck.dealCard();
        deck.addToTable(card);
        boolean isWin = checkWin(callbackData, card);

        if (isWin) {
            gameCallback.sendGameMessage(
                    chatId,
                    deck.getTableAsString() + "\nПоздравляем, вы выиграли!",
                    null
            );

            if (deck.roundNumber == 4) {
                handleGameOver(true);
            } else {
                deck.roundNumber++;
                play();
            }
        } else {
            handleGameOver(false);
        }
    }

    /**
     * Обработка конца игры
     */
    private void handleGameOver(boolean isWinner) {
        if (gameCallback == null) return;

        if (isWinner) {
            gameCallback.sendGameMessage(
                    chatId,
                    deck.getTableAsString() + "\nВы прошли все раунды! 🎉\nХотите выбрать другую игру?",

                    keyboardFactory.createGameSelectionKeyboard()
            );
        } else {
            gameCallback.sendGameMessage(
                    chatId,
                    deck.getTableAsString() + "\nК сожалению, вы проиграли! Хотите сыграть снова?",
                    keyboardFactory.createGameSelectionKeyboard()
            );
        }
        resetGame();
    }
}