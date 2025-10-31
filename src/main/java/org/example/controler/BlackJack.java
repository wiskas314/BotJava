package org.example.controler;

import org.example.controler.cards.Card;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.example.controler.cards.Deck;

/**
 * Класс, реализующий игру Black Jack
 */
public class BlackJack implements Game{
    private Deck deck;
    private final KeyboardFactory keyboardFactory;
    private String chatId;
    private boolean isGameOver;
    private boolean isPlayerTurn;
    private int playerScore;
    private int dealerScore;
    private Card[] dealerHand;
    private Card[] playerHand;

    public BlackJack() {
        keyboardFactory = new KeyboardFactory();
        isGameOver = false;
        isPlayerTurn = true;
        playerScore = 0;
        dealerScore = 0;
        deck = new Deck();
        deck.initializeDeck();
        dealerHand = new Card[5];
        playerHand = new Card[5];
    }

    /**
     * Начало игры
     */
    @Override
    public void startGame(String chatId, TelegramBot bot) {
        this.chatId = chatId;
        dealInitialCards();
        sendGameState(bot);
    }

    @Override
    public boolean getIsGameOver(){return isGameOver;}

    /**
     * Сброс состояния игры
     */
    private void resetGame() {
        deck = new Deck();
        deck.initializeDeck();
        playerScore = 0;
        dealerScore = 0;
        isGameOver = false;
        isPlayerTurn = true;
        dealerHand = new Card[5];
        playerHand = new Card[5];
    }

    /**
     * Раздача начальных карт (по 2 каждому)
     */
    private void dealInitialCards() {
        dealerHand[0] = deck.dealCard();
        dealerHand[1] = deck.dealCard();
        playerHand[0] = deck.dealCard();
        playerHand[1] = deck.dealCard();

        playerScore = calculateScore(playerHand);
        dealerScore = calculateScore(dealerHand);
    }


    private String getHandAsString(Card[] hand, boolean showAll) {
        String handAsString = "";
        for (int i = 0; i < hand.length; i++) {
            Card card = hand[i];
            if (card == null) break;

            if (i == 1 && !showAll) {
                handAsString += "\uD83C\uDCCF";
            } else {
                handAsString += card.getCard();
            }
        }
        return handAsString;
    }

    /**
     * Расчёт очков по картам
     */
    private int calculateScore(Card[] hand) {
        int score = 0;
        int aces = 0;

        for (Card card : hand) {
            if (!(card == null)) {
                int value = card.getValue();
                if (value == 14) { // Туз
                    aces++;
                    score += 11;
                } else if (value > 10) {
                    score += 10;
                } else {
                    score += value;
                }
            } else {
                break;
            }
        }

        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }

    /**
     * Отправка текущего состояния игры
     */
    private void sendGameState(TelegramBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        String gameState = "🃏 **Black Jack** 🃏\n\n";
        gameState += "Ваши карты:  " + getHandAsString(playerHand, true) + "  (Сумма: " + playerScore + ")\n";

        boolean showDealerAll = !isPlayerTurn || isGameOver;
        gameState += "Карты дилера:  " + getHandAsString(dealerHand, showDealerAll);

        if (isPlayerTurn && !isGameOver) {
            int visibleDealerScore = dealerHand[0] != null ? dealerHand[0].getValue() : 0;
            gameState += "  (Сумма: " + visibleDealerScore + ")\n\n";
        } else {
            gameState += "  (Сумма: " + dealerScore + ")\n\n";
        }

        if (!isGameOver) {
            if (isPlayerTurn) {
                gameState += "Ваш ход! Хотите взять ещё карту?";
                message.setReplyMarkup(keyboardFactory.createHitOrStandKeyboard(chatId).getReplyMarkup());
            } else {
                gameState += "Ход дилера...";
            }
        } else {
            gameState += determineWinner();
            message.setReplyMarkup(keyboardFactory.createGameSelectionKeyboard(chatId).getReplyMarkup());
        }

        message.setText(gameState);
        bot.send(message);
    }

    /**
     * Обработка выбора пользователя (hit или stand)
     */
    @Override
    public void processUserChoice(String callbackData, TelegramBot bot) {
        if (isGameOver) return;

        if ("hit".equals(callbackData)) {
            for (int i = 0; i < 5; i++ ){
                if (playerHand[i] == null){
                    playerHand[i] = deck.dealCard();
                    break;
                }
            }
            playerScore = calculateScore(playerHand);

            if (playerScore > 21) {
                isGameOver = true;
                sendGameState(bot);
            } else {
                sendGameState(bot);
            }
        } else if ("stand".equals(callbackData)) {
            isPlayerTurn = false;
            dealerTurn(bot);
        }
    }

    /**
     * Ход дилера (автоматический)
     */
    private void dealerTurn(TelegramBot bot) {
        while (dealerScore < 17 && dealerScore < playerScore) {
            for (int i = 0; i < 5; i++ ){
                if (dealerHand[i] == null){
                    dealerHand[i] = deck.dealCard();
                    break;
                }
            }
            dealerScore = calculateScore(dealerHand);
        }
        isGameOver = true;
        sendGameState(bot);
    }

    /**
     * Определение победителя
     */
    private String determineWinner() {
        if (playerScore > 21) {
            return "Вы перебрали! Проигрыш. 😞";
        } else if (dealerScore > 21) {
            return "Дилер перебрал! Вы выиграли! 🎉";
        } else if (playerScore > dealerScore) {
            return "Вы набрали больше очков! Победа! 🎉";
        } else if (dealerScore > playerScore) {
            return "Дилер набрал больше очков! Проигрыш. 😞";
        } else {
            return "Ничья! 🤝";
        }
    }
}
