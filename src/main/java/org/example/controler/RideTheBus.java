package org.example.controler;

import org.example.controler.cards.Card;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.example.controler.cards.Deck;



/**
 * Класс Реализующий игру в Ride The Bus
 */
public class RideTheBus implements Game{
    private Deck deck;
    private String specialCard;
    private final KeyboardFactory keyboardFactory;
    private String chatId;
    private boolean isGameOver;
    private Card[] table;
    private int roundNumber;

    public RideTheBus() {
        keyboardFactory = new KeyboardFactory();
        specialCard = "\uD83C\uDCCF";
        isGameOver = false;
        deck = new Deck();
        table = new Card[4];
        roundNumber = 1;
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
    @Override
    public void startGame(String chatId, TelegramBot bot) {
        this.chatId = chatId;
        play(bot);
    }
    /**
     *Геттер состояния игры
     */
    @Override
    public boolean getIsGameOver(){return isGameOver;}
    /**
     *Сброс состояния игры
     */
    private void resetGame() {
        roundNumber = 1;
        deck = new Deck();
        deck.initializeDeck();

        isGameOver = false;
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
    @Override
    public void processUserChoice(String callbackData, TelegramBot bot) {
        if (isGameOver) {
            return;
        }

        Card card = deck.dealCard();
        addToTable(card);
        boolean isWin = checkWin(callbackData, card);
        SendMessage winMessage = new SendMessage();
        winMessage.setChatId(chatId);

        if (isWin) {
            if (roundNumber == 4) {
                handleGameOver(bot, true);
            } else {
                roundNumber += 1;
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
            message.setText(getTableAsString() + "\nВы прошли все раунды! 🎉\nВыберите игру:");
        } else {
            message.setText(getTableAsString() + "\nК сожалению, вы проиграли! Выберите игру:");
        }

        message.setReplyMarkup(keyboardFactory.createGameSelectionKeyboard(chatId).getReplyMarkup());
        bot.send(message);
        resetGame();
    }



}