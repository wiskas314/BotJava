package org.example.controler.game;


import org.example.controler.KeyboardBuilder;
import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.game.cards.Card;
import org.example.controler.game.cards.Deck;
import org.example.controler.db.UserService;
import org.example.controler.dto.ButtonData;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс Реализующий игру в Ride The Bus
 */
public class RideTheBus implements Game {
    private Deck deck;
    private String specialCard;
    private final KeyboardBuilder keyboardBuilder;
    private String chatId;
    private MessageSender gameCallback;
    private boolean isGameOver;
    private Card[] table;
    private int roundNumber;
    private final UserService userService;
    private boolean betPlaced;
    private int currentBet;
    private int currentMultiplier;
    private final int[] multipliers = {1, 2, 3, 5, 8};

    public RideTheBus() {
        keyboardBuilder = new KeyboardBuilder();
        specialCard = "\uD83C\uDCCF";
        isGameOver = false;
        deck = new Deck();
        table = new Card[4];
        userService = new UserService();
        currentBet = 0;
        currentMultiplier = 1;
        roundNumber = 1;
    }

    /**
     * Установка пользователя
     */
    public void setGameCallback(MessageSender callback) {
        this.gameCallback = callback;
    }

    /**
     * Добавление карты на стол
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
     * Получение карт на столе как строку, а не как массив Card
     */
    public String getTableAsString() {
        String tableAsString = "";
        for (Card tableCard : table) {
            if (tableCard == null) break;
            tableAsString += tableCard.getCard();
        }
        return tableAsString;
    }

    /**
     * Проверка на победу
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
                        (cardSuit.equals("♠️") && message.equals("peaks")) ||
                        (cardSuit.equals("♣️") && message.equals("clubs")));
        }
        return true;
    }

    @Override
    public GameResponse startGame(String chatId) {
        this.chatId = chatId;
        int balance = userService.getUserBalance(Long.valueOf(chatId));
        if (!betPlaced) {
            if (balance == 0) {
                return new GameResponse(
                        new GameMessage(
                                chatId,
                                "Вам нужно пополнить баланс",
                                keyboardBuilder.createReplenishKeyboard()
                        ),
                        true
                );
            } else {
                return new GameResponse(
                        new GameMessage(
                                chatId,
                                "Ваш баланс " + balance + " 🪙\nСделайте ставку для начала игры",
                                keyboardBuilder.createBetKeyboard()
                        ),
                        false
                );
            }
        } else {
            return play();
        }
    }

    @Override
    public boolean getIsGameOver() {
        return isGameOver;
    }

    /**
     * Сброс состояния игры
     */
    private void resetGame() {
        roundNumber = 1;
        deck = new Deck();
        deck.initializeDeck();
        betPlaced = false;
        currentBet = 0;
        currentMultiplier = 1;
    }

    /**
     * Метод реализующий интерфейс во время игры
     */
    private GameResponse play() {
        currentMultiplier = multipliers[roundNumber - 1];
        String roundText = "";
        List<List<ButtonData>> keyboard = null;

        switch (roundNumber) {
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


        GameMessage message = new GameMessage(roundText + "\n" + getTableAsString() + " " + specialCard, chatId, keyboard);
        return new GameResponse(message, false);

    }

    @Override
    public GameResponse processUserChoice(String callbackData) {
        if (isGameOver || gameCallback == null) {
            return new GameResponse(null, true);
        }
        if (callbackData.equals("exit")) {
            int winAmount = currentBet * currentMultiplier;
            boolean success = userService.payWinnings(Long.valueOf(chatId), winAmount);

            if (success) {
                int newBalance = userService.getUserBalance(Long.valueOf(chatId));
                userService.changeWinAndEarnedRideTheBus(Long.valueOf(chatId), winAmount - currentBet);
                userService.changeEarned(Long.valueOf(chatId), winAmount - currentBet);

                String messageText = "🎉 Вы забрали выигрыш!\n" +
                        "💎 Выигрыш: " + winAmount + " 🪙\n" +
                        "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                        "Хотите сыграть еще?";

                resetGame();
                return new GameResponse(
                        new GameMessage(chatId, messageText, keyboardBuilder.createGameSelectionButtons()),
                        true
                );
            } else {
                return new GameResponse(
                        new GameMessage(chatId, "❌ Ошибка при выплате выигрыша", null),
                        true
                );
            }
        }
        Card card = deck.dealCard();
        addToTable(card);
        boolean isWin = checkWin(callbackData, card);

        if (isWin) {
            if (roundNumber == 4) {
                return handleGameOver(true);
            } else {
                roundNumber = roundNumber + 1;
                return play();
            }
        } else {
            return handleGameOver(false);
        }

    }

    @Override
    public GameResponse processBet(String callbackData) {
        if (betPlaced) {
            return new GameResponse(
                    new GameMessage(chatId, "Ставка уже размещена", null),
                    false
            );
        }

        try {
            int bet = 0;
            if (callbackData.equals("bet_all")) {
                bet = userService.getUserBalance(Long.valueOf(chatId));
            } else {
                bet = Integer.parseInt(callbackData.replace("bet_", ""));
            }

            if (userService.canPlaceBet(Long.valueOf(chatId), bet)) {
                if (userService.placeBet(Long.valueOf(chatId), bet)) {
                    currentBet = bet;
                    betPlaced = true;

                    return play();
                } else {
                    return new GameResponse(
                            new GameMessage(chatId, "❌ Ошибка при размещении ставки", null),
                            false
                    );
                }
            } else {
                int balance = userService.getUserBalance(Long.valueOf(chatId));
                String messageText = "❌ Недостаточно средств для ставки " + bet + " 🪙\n💰 Ваш баланс: " + balance + " 🪙";
                return new GameResponse(
                        new GameMessage(chatId, messageText, keyboardBuilder.createBetKeyboard()),
                        false
                );
            }
        } catch (NumberFormatException e) {
            return new GameResponse(
                    new GameMessage(chatId, "❌ Неверный формат ставки", null),
                    false
            );
        }
    }
    /**
     *создает динамическую клавиатуру с кнопками выбора соответствующими текущему раунду игры
     */
    private List<List<ButtonData>> createDynamicRoundKeyboard(){
        List<List<ButtonData>> buttonRows = new ArrayList<>();
        switch (roundNumber){
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
     * Обработка конца игры
     */
    private GameResponse handleGameOver(boolean isWinner) {
        if (isWinner) {
            int winAmount = currentBet * 10; // Множитель для полной победы
            boolean success = userService.payWinnings(Long.valueOf(chatId), winAmount);
            if (success) {
                int newBalance = userService.getUserBalance(Long.valueOf(chatId));
                userService.changeWinAndEarnedRideTheBus(Long.valueOf(chatId), currentBet * 9);
                userService.changeEarned(Long.valueOf(chatId), currentBet * 9);

                String messageText = getTableAsString() +
                        "\n🎉 Поздравляем! Вы прошли все раунды!\n" +
                        "💎 Выигрыш: " + winAmount + " 🪙\n" +
                        "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                        "Хотите сыграть еще?";

                resetGame();
                return new GameResponse(
                        new GameMessage(chatId, messageText, keyboardBuilder.createGameSelectionButtons()),
                        true
                );
            } else {
                resetGame();
                return new GameResponse(
                        new GameMessage(chatId, "❌ Ошибка при выплате выигрыша", null),
                        true
                );
            }
        } else {
            userService.changeLossesAndLostRideTheBus(Long.valueOf(chatId), currentBet);
            Card lastCard = table[table.length - 1];

            String messageText = "❌ Неверно! Карта: " + (lastCard != null ? lastCard.getCard() : "") +
                    "\n" + getTableAsString() +
                    "\nК сожалению, вы проиграли! Хотите сыграть снова?";

            resetGame();
            return new GameResponse(
                    new GameMessage(chatId, messageText, keyboardBuilder.createGameSelectionButtons()),
                    true
            );
        }
    }
}