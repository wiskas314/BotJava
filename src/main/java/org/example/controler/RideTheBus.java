package org.example.controler;

import org.example.game.Card;
import org.example.game.Deck;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


import java.util.Random;

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
     *Проверка на победу
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
        deck.roundNumber = 1;
        deck = new Deck(4);
        deck.initializeDeck();

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

        switch (deck.roundNumber) {
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
            message.setText(message.getText() + "\n" + deck.getTableAsString() + " " + specialCard);
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

        Card card = deck.dealCard();
        deck.addToTable(card);
        boolean isWin = checkWin(callbackData, card);
        SendMessage winMessage = new SendMessage();
        winMessage.setChatId(chatId);

        if (isWin) {
            winMessage.setText(deck.getTableAsString() + "\nПоздравляем, вы выиграли!");
            bot.send(winMessage);

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
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        if (isWinner) {
            message.setText(deck.getTableAsString() + "\nВы прошли все раунды! 🎉\nХотите выбрать другую игру?");
        } else {
            message.setText(deck.getTableAsString() + "\nК сожалению, вы проиграли! Хотите сыграть снова?");
        }

        message.setReplyMarkup(keyboardFactory.createGameSelectionKeyboard(chatId).getReplyMarkup());
        bot.send(message);
        resetGame();
    }



}