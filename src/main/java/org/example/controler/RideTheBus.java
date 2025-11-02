package org.example.controler;

import org.example.controler.cards.Card;
import org.example.controler.cards.Deck;
import org.example.controler.db.UserService;


/**
 * Класс Реализующий игру в Ride The Bus
 */
public class RideTheBus {
    private Deck deck;
    private String specialCard;
    private final KeyboardFactory keyboardFactory;
    private String chatId;
    private Long userID;
    private boolean isGameOver;
    private final UserService userService;
    private boolean isProcessing;
    private boolean betPlaced;
    private int currentBet;
    private int currentMultiplier;
    private final int[] multipliers = {1, 2, 3, 5, 10};

    public RideTheBus() {
        keyboardFactory = new KeyboardFactory();
        specialCard = "\uD83C\uDCCF";
        isGameOver = false;
        isProcessing = false;
        deck = new Deck(4);
        userService = new UserService();
        currentBet = 0;
        currentMultiplier = 1;
    }
    /**
     * Установка пользователя
     */
    public void setUser(Long userId) {
        this.userID = userId;
        System.out.println("User set: " + userId); // Для отладки
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
        int balance = userService.getUserBalance(Long.valueOf(chatId));
        if (!betPlaced) {
            if(balance==0){
                bot.sendMessage("Вам нужно пополнить баланс",chatId,keyboardFactory.createReplenishKeyboard());
            }else{
                bot.sendMessage("Ваш баланс " + balance + " Сделайте ставку для начала игры",
                        chatId, keyboardFactory.createBetKeyboard());
            }
        }else{
            play(bot);
        }
    }
    /**
     * Сброс состояния игры
     */
    private void resetGame() {
        deck.roundNumber = 1;
        deck = new Deck(4);
        deck.initializeDeck();
        betPlaced = false;
        isGameOver = false;
        isProcessing = false;
        currentBet = 0;
        currentMultiplier = 1; // Сброс флага обработки
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
        currentMultiplier = multipliers[deck.roundNumber - 1];
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
        if (callbackData.equals("exit")) {
            int winAmount = currentBet * currentMultiplier;
            boolean success = userService.payWinnings(userID, winAmount);

            if (success) {
                int newBalance = userService.getUserBalance(userID);
                bot.sendMessage("🎉 Вы забрали выигрыш!\n" +
                                "💎 Выигрыш: " + winAmount + " 🪙\n" +
                                "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                                "Хотите сыграть еще?",
                        chatId,
                        keyboardFactory.createGameSelectionKeyboard());
            } else {
                bot.sendMessage("❌ Ошибка при выплате выигрыша", chatId, null);
            }
            resetGame();
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
     * Обработка ставки
     */
    public void processBet(String callbackData, TelegramBot bot) {
        if (betPlaced) {
            return;
        }

        try {
            int bet = 0;
            if (callbackData.equals("bet_all")) {
                bet = userService.getUserBalance(userID);
            } else {
                bet = Integer.parseInt(callbackData.replace("bet_", ""));
            }

            if (userService.canPlaceBet(userID, bet)) {
                if (userService.placeBet(userID, bet)) {
                    currentBet = bet;
                    betPlaced = true;
                    int balance = userService.getUserBalance(userID);
                    bot.sendMessage("✅ Ставка " + bet + " 🪙 принята!\n💰 Текущий баланс: " + balance + " 🪙\n\nНачинаем игру Ride The Bus!",
                            chatId, null);
                    play(bot);
                } else {
                    bot.sendMessage("❌ Ошибка при размещении ставки", chatId, null);
                }
            } else {
                int balance = userService.getUserBalance(userID);
                bot.sendMessage("❌ Недостаточно средств для ставки " + bet + " 🪙\n💰 Ваш баланс: " + balance + " 🪙",
                        chatId, keyboardFactory.createBetKeyboard());
            }
        } catch (NumberFormatException e) {
            bot.sendMessage("❌ Неверный формат ставки", chatId, null);
        }
    }

    /**
     * Обработка конца игры
     */
    private void handleGameOver(TelegramBot bot, boolean isWinner) {
        if (isWinner) {
            int winAmount = currentBet * 10; // Множитель для полной победы
            boolean success = userService.payWinnings(userID, winAmount);

            if (success) {
                int newBalance = userService.getUserBalance(userID);
                bot.sendMessage(deck.getTableAsString() +
                                "\n🎉 Поздравляем! Вы прошли все раунды!\n" +
                                "💎 Выигрыш: " + winAmount + " 🪙\n" +
                                "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                                "Хотите сыграть еще?",
                        chatId,
                        keyboardFactory.createGameSelectionKeyboard());
            } else {
                bot.sendMessage("❌ Ошибка при выплате выигрыша", chatId, null);
            }
        } else {
            Card lastCard = deck.table[deck.table.length - 1];
            bot.sendMessage("❌ Неверно! Карта: " + (lastCard != null ? lastCard.getCard() : "") +
                            "\n" + deck.getTableAsString() +
                            "\nК сожалению, вы проиграли! Хотите сыграть снова?",
                    chatId,
                    keyboardFactory.createGameSelectionKeyboard());
        }
        resetGame();
    }
}