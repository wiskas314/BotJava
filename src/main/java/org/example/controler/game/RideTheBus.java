package org.example.controler.game;


import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.cards.Card;
import org.example.controler.cards.Deck;
import org.example.controler.db.UserService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Класс Реализующий игру в Ride The Bus
 */
public class RideTheBus implements Game {
    private Deck deck;
    private String specialCard;
    private final KeyboardFactory keyboardFactory;
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
        keyboardFactory = new KeyboardFactory();
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
    public void startGame(String chatId) {
        this.chatId = chatId;
        int balance = userService.getUserBalance(Long.valueOf(chatId));
        if (!betPlaced) {
            if(balance==0){
                gameCallback.sendMessage("Вам нужно пополнить баланс",chatId,keyboardFactory.createReplenishKeyboard());
            }else{
                gameCallback.sendMessage("Ваш баланс " + balance + " Сделайте ставку для начала игры",
                        chatId, keyboardFactory.createBetKeyboard());
            }
        }else{
            play();
        }
    }

    @Override
    public boolean getIsGameOver(){return isGameOver;}

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
    private void play() {
        currentMultiplier = multipliers[roundNumber - 1];
        String roundText = "";
        InlineKeyboardMarkup keyboard = null;

        switch (roundNumber) {
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
            gameCallback.sendMessage(roundText + "\n" + getTableAsString() + " " + specialCard, chatId, keyboard);
        }
    }

    @Override
    public void processUserChoice(String callbackData) {
        if (isGameOver || gameCallback == null) {
            return;
        }

        if (callbackData.equals("exit")) {
            int winAmount = currentBet * currentMultiplier;
            boolean success = userService.payWinnings(Long.valueOf(chatId), winAmount);

            if (success) {
                int newBalance = userService.getUserBalance(Long.valueOf(chatId));
                userService.changeWinAndEarnedRideTheBus(Long.valueOf(chatId), winAmount-currentBet);
                userService.changeEarned(Long.valueOf(chatId),winAmount-currentBet);
                gameCallback.sendMessage("🎉 Вы забрали выигрыш!\n" +
                                "💎 Выигрыш: " + winAmount + " 🪙\n" +
                                "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                                "Хотите сыграть еще?",
                        chatId,
                        keyboardFactory.createGameSelectionKeyboard());
            } else {
                gameCallback.sendMessage("❌ Ошибка при выплате выигрыша", chatId, null);
            }
            resetGame();
            return;
        }
        Card card = deck.dealCard();
        addToTable(card);
        boolean isWin = checkWin(callbackData, card);

        if (isWin) {
            gameCallback.sendMessage(getTableAsString() + "\nПоздравляем, вы выиграли!",chatId,null);

            if (roundNumber == 4) {
                handleGameOver(true);
            } else {
                roundNumber=roundNumber+1;
                play();
            }
        } else {
            handleGameOver(false);
        }

    }

    @Override
    public void processBet(String callbackData) {
        if (betPlaced) {
            return;
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
                    int balance = userService.getUserBalance(Long.valueOf(chatId));
                    gameCallback.sendMessage("✅ Ставка " + bet + " 🪙 принята!\n💰 Текущий баланс: " + balance + " 🪙\n\nНачинаем игру Ride The Bus!",
                            chatId, null);
                    play();
                } else {
                    gameCallback.sendMessage("❌ Ошибка при размещении ставки", chatId, null);
                }
            } else {
                int balance = userService.getUserBalance(Long.valueOf(chatId));
                gameCallback.sendMessage("❌ Недостаточно средств для ставки " + bet + " 🪙\n💰 Ваш баланс: " + balance + " 🪙",
                        chatId, keyboardFactory.createBetKeyboard());
            }
        } catch (NumberFormatException e) {
            gameCallback.sendMessage("❌ Неверный формат ставки", chatId, null);
        }
    }

    /**
     * Обработка конца игры
     */
    private void handleGameOver(boolean isWinner) {
        if (isWinner) {
            int winAmount = currentBet * 10; // Множитель для полной победы
            boolean success = userService.payWinnings(Long.valueOf(chatId), winAmount);

            if (success) {
                int newBalance = userService.getUserBalance(Long.valueOf(chatId));
                userService.changeWinAndEarnedRideTheBus(Long.valueOf(chatId), currentBet * 9);
                userService.changeEarned(Long.valueOf(chatId), currentBet * 9);
                gameCallback.sendMessage(getTableAsString() +
                                "\n🎉 Поздравляем! Вы прошли все раунды!\n" +
                                "💎 Выигрыш: " + winAmount + " 🪙\n" +
                                "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                                "Хотите сыграть еще?",
                        chatId,
                        keyboardFactory.createGameSelectionKeyboard());
            } else {
                gameCallback.sendMessage("❌ Ошибка при выплате выигрыша", chatId, null);
            }
        } else {
            userService.changeLossesAndLostRideTheBus(Long.valueOf(chatId), currentBet);
            Card lastCard = table[table.length - 1];
            gameCallback.sendMessage("❌ Неверно! Карта: " + (lastCard != null ? lastCard.getCard() : "") +
                            "\n" + getTableAsString() +
                            "\nК сожалению, вы проиграли! Хотите сыграть снова?",
                    chatId,
                    keyboardFactory.createGameSelectionKeyboard());
        }
        resetGame();
        isGameOver = true;
    }
}