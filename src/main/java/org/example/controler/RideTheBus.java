package org.example.controler;

import org.example.controler.cards.Card;
import org.example.controler.cards.Deck;
import org.example.controler.dto.ButtonData;
import org.example.controler.dto.GameMessage;
import org.example.controler.dto.GameResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс Реализующий игру в Ride The Bus
 */
public class RideTheBus implements Game {
    private Deck deck;
    private String specialCard;
    private final KeyboardFactory keyboardFactory;
    private String chatId;
    private boolean isGameOver;
    private boolean isProcessing;
    private MessageSender gameCallback;

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
                        (cardSuit.equals("♠️") && message.equals("peaks")) ||
                        (cardSuit.equals("♣️") && message.equals("clubs")));
        }
        return true;
    }

    /**
     * Начало игры
     */
    @Override
    public GameResponse startGame(String chatId) {
        this.chatId = chatId;
        this.isGameOver = false;
        return play();
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
    @Override
    public boolean IsGameOver(){
        return isGameOver;
    }

    @Override
    public String getGameType() {
        return "RideTheBus";
    }

    /**
     * Метод реализующий интерфейс во время игры
     */
    private GameResponse play() {

        String roundText = "";
        List<List<ButtonData>> keyboard = null;

        switch (deck.roundNumber) {
            case 1:
                roundText = "Раунд 1 \nВыберите цвет:";
                keyboard = createDynamicRoundKeyboard();
                break;

            case 2:
                roundText = "Раунд 2 \nВыберите будет ли следующая карта старшей или младшей масти:";
                keyboard = createDynamicRoundKeyboard();
                break;

            case 3:
                roundText = "Раунд 3 \nВыберите будет ли следующая карта внутри или вне диапазона:";
                keyboard = createDynamicRoundKeyboard();
                break;

            case 4:
                roundText = "Раунд 4 \nВыберите какой масти будет следующая карта:";
                keyboard = createDynamicRoundKeyboard();
                break;
        }

        GameMessage message = new GameMessage(chatId,roundText + "\n" + deck.getTableAsString() + " " + specialCard,keyboard);
        return new GameResponse(message,false);
    }
    private List<List<ButtonData>> createDynamicRoundKeyboard(){
        List<List<ButtonData>> buttonRows = new ArrayList<>();
        switch (deck.roundNumber){
            case 1:
                List<ButtonData> round1Butons=new ArrayList<>();
                round1Butons.add(new ButtonData("Красный","red"));
                round1Butons.add(new ButtonData("Черный","black"));
                buttonRows.add(round1Butons);
                break;
            case 2:
                List<ButtonData> round2Butons=new ArrayList<>();
                round2Butons.add(new ButtonData("Выше","higher"));
                round2Butons.add(new ButtonData("Ниже","lower"));
                buttonRows.add(round2Butons);
                break;
            case 3:
                List<ButtonData> round3Butons=new ArrayList<>();
                round3Butons.add(new ButtonData("Внутри диапазона","inside"));
                round3Butons.add(new ButtonData("Вне диапазона","outside"));
                buttonRows.add(round3Butons);
                break;
            case 4:
                List<ButtonData> round4Butons =new ArrayList<>();
                round4Butons.add(new ButtonData("♥ Черви","hearts"));
                round4Butons.add(new ButtonData("♦ Бубны","diamonds"));
                buttonRows.add(round4Butons);
                List<ButtonData> secondround4Butons =new ArrayList<>();
                secondround4Butons.add(new ButtonData("♣ Трефы","clubs"));
                secondround4Butons.add(new ButtonData("♠ Пики","peaks"));
                buttonRows.add(secondround4Butons);
                break;
        }
        return buttonRows;
    }
    /**
     * Создание кнопок для выбора игры
     */
    private List<List<ButtonData>> createGameSelectionButtons() {
        List<List<ButtonData>> buttons = new ArrayList<>();
        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("🎮 Ride the Bus", "ride_the_bus"));
        buttons.add(row);
        return buttons;
    }

    /**
     * Метод для обработки выбора пользователя
     */
    public GameResponse processUserChoice(String callbackData) {

        if (isGameOver) {
            return new GameResponse(
                    new GameMessage(chatId, "Игра уже завершена", null),
                    true
            );
        }

        Card card = deck.dealCard();
        deck.addToTable(card);
        boolean isWin = checkWin(callbackData, card);

        if (isWin) {
            if (deck.roundNumber == 4) {
                isGameOver = true;
                return handleGameOver(true);
            } else {
                deck.roundNumber++;
                return play();
            }
        } else {
            isGameOver = true;
            return handleGameOver(false);
        }
    }



    /**
     * Обработка конца игры
     */
    private GameResponse handleGameOver(boolean isWinner) {
        String messageText;
        if (isWinner) {
            messageText = deck.getTableAsString() + "\nВы прошли все раунды! 🎉\nХотите выбрать другую игру?";
        } else {
            messageText = deck.getTableAsString() + "\nК сожалению, вы проиграли! Хотите сыграть снова?";
        }

        List<List<ButtonData>> keyboardButtons = createGameSelectionButtons();
        GameMessage message = new GameMessage(chatId, messageText, keyboardButtons);

        resetGame();
        return new GameResponse(message, true);
    }
}