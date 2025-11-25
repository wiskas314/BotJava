package org.example.controler.game;

import org.example.controler.KeyboardFactory;
import org.example.controler.MessageSender;
import org.example.controler.cards.Card;
import org.example.controler.cards.Deck;
import org.example.controler.db.UserService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * Класс, реализующий игру Black Jack
 */
public class BlackJack implements Game {
    private Deck deck;
    private final KeyboardFactory keyboardFactory;
    private String chatId;
    private MessageSender gameCallback;
    private boolean isGameOver;
    private boolean isPlayerTurn;
    private int playerScore;
    private int dealerScore;
    private Card[] dealerHand;
    private Card[] playerHand;
    private int currentBet;
    private boolean betPlaced;
    private final UserService userService;

    public BlackJack() {
        keyboardFactory = new KeyboardFactory();
        isGameOver = false;
        isPlayerTurn = true;
        playerScore = 0;
        dealerScore = 0;
        currentBet = 0;
        betPlaced = false;
        userService = new UserService();
        deck = new Deck();
        deck.initializeDeck();
        dealerHand = new Card[5];
        playerHand = new Card[5];
    }

    /**
     * Устанавливает callback для взаимодействия с внешним миром
     */
    public void setGameCallback(MessageSender callback) {
        this.gameCallback = callback;
    }
    /**
     * Установка пользователя (требуется для работы со ставками)
     */
    @Override
    public void startGame(String chatId) {
        this.chatId = chatId;

        if (!betPlaced) {
            int balance = userService.getUserBalance(Long.valueOf(chatId));
            if (balance == 0) {
                gameCallback.sendMessage(
                        "Вам нужно пополнить баланс",
                        chatId,
                        keyboardFactory.createReplenishKeyboard()
                );
            } else {
                gameCallback.sendMessage(
                        "Ваш баланс: " + balance + " 🪙\nСделайте ставку для начала игры",
                        chatId,
                        keyboardFactory.createBetKeyboard()
                );
            }
        } else {
            dealInitialCards();
            sendGameState();
        }
    }

    @Override
    public boolean getIsGameOver() {
        return isGameOver;
    }

    /**
     * Сброс состояния игры (включая ставки)
     */
    private void resetGame() {
        deck = new Deck();
        deck.initializeDeck();
        playerScore = 0;
        dealerScore = 0;
        isGameOver = false;
        isPlayerTurn = true;
        currentBet = 0;
        betPlaced = false;
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

    /**
     * Получить руку как строку
     */
    private String getHandAsString(Card[] hand, boolean showAll) {
        StringBuilder handAsString = new StringBuilder();
        for (int i = 0; i < hand.length; i++) {
            Card card = hand[i];
            if (card == null) break;

            if (i == 1 && !showAll) {
                handAsString.append("\uD83C\uDCCF");
            } else {
                handAsString.append(card.getCard());
            }
            handAsString.append("  ");

        }
        return handAsString.toString();
    }

    /**
     * Расчёт очков по картам
     */
    private int calculateScore(Card[] hand) {
        int score = 0;
        int aces = 0;

        for (Card card : hand) {
            if (card != null) {
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
    private void sendGameState() {
        String gameState = "🃏 **Black Jack** 🃏\n\n";
        gameState += "Ваши карты:  " + getHandAsString(playerHand, true) + "  (Сумма: " + playerScore + ")\n";

        boolean showDealerAll = !isPlayerTurn || isGameOver;
        gameState += "Карты дилера:  " + getHandAsString(dealerHand, showDealerAll);

        if (isPlayerTurn && !isGameOver) {
            int visibleDealerScore = 0;
            if (dealerHand[0] != null) {
                int value = dealerHand[0].getValue();
                if (value == 14) {
                    visibleDealerScore = 11;
                } else if (value > 10) {
                    visibleDealerScore = 10;
                } else {
                    visibleDealerScore = value;
                }
            }
            gameState += "  (Сумма: " + visibleDealerScore + ")\n\n";
        } else {
            gameState += "  (Сумма: " + dealerScore + ")\n\n";
        }

        InlineKeyboardMarkup markup = null;
        if (!isGameOver) {
            if (isPlayerTurn) {
                gameState += "Ваш ход! Хотите взять ещё карту?";
                markup = keyboardFactory.createHitOrStandKeyboard();
            } else {
                gameState += "Ход дилера...";
            }
        } else {
            gameState += determineWinner();
            markup = keyboardFactory.createGameSelectionKeyboard();
        }

        gameCallback.sendMessage(gameState, chatId, markup);
    }

    @Override
    public void processUserChoice(String callbackData) {
        if (isGameOver) return;

        if ("hit".equals(callbackData)) {
            for (int i = 0; i < 5; i++) {
                if (playerHand[i] == null) {
                    playerHand[i] = deck.dealCard();
                    break;
                }
            }
            playerScore = calculateScore(playerHand);

            if (playerScore > 21) {
                isGameOver = true;
                sendGameState();
                handleGameOver(false);
            } else {
                sendGameState();
            }
        } else if ("stand".equals(callbackData)) {
            isPlayerTurn = false;
            dealerTurn();
        }
    }

    /**
     * Ход дилера (автоматический)
     */
    private void dealerTurn() {
        while (dealerScore < 17 && dealerScore < playerScore) {
            for (int i = 0; i < 5; i++) {
                if (dealerHand[i] == null) {
                    dealerHand[i] = deck.dealCard();
                    break;
                }
            }
            dealerScore = calculateScore(dealerHand);
        }
        isGameOver = true;
        sendGameState();
        handleGameOver(determineIfPlayerWon());
    }

    /**
     * Определение, выиграл ли игрок (без текста — только boolean)
     */
    private boolean determineIfPlayerWon() {
        if (playerScore > 21) return false;
        if (dealerScore > 21) return true;
        if (playerScore > dealerScore) return true;
        if (dealerScore > playerScore) return false;
        return true;
    }

    /**
     * Обработка конца игры: выплата выигрыша или сброс
     */
    private void handleGameOver(boolean isWinner) {
        int winAmount = 0;
        String resultMessage;

        if (isWinner) {
            if (playerScore == 21 && playerHand[2] == null) {
                winAmount = (int) Math.floor(currentBet * 1.5);
                userService.changeWinsAndEarnedBlackJack(Long.valueOf(chatId), (int)Math.floor(currentBet * 0.5));
                userService.changeEarned(Long.valueOf(chatId),(int)Math.floor(currentBet * 0.5));
                resultMessage = "🎉 **Блэкджек!** Вы выиграли с натуральной 21!\n" +
                        "💎 Выигрыш: " + winAmount + " 🪙\n";
            } else if (playerScore == dealerScore) {
                winAmount = currentBet;
                userService.changeWinsAndEarnedBlackJack(Long.valueOf(chatId), 0);
                resultMessage = "🤝 **Ничья!** Ставка возвращается.\n" +
                        "💎 Возврат: " + winAmount + " 🪙\n";
            } else {
                winAmount = currentBet * 2;
                userService.changeWinsAndEarnedBlackJack(Long.valueOf(chatId), currentBet);
                userService.changeEarned(Long.valueOf(chatId),currentBet);
                resultMessage = "🎉 **Вы выиграли!**\n" +
                        "💎 Выигрыш: " + winAmount + " 🪙\n";
            }

            boolean success = userService.payWinnings(Long.valueOf(chatId), winAmount);
            if (success) {
                int newBalance = userService.getUserBalance(Long.valueOf(chatId));
                resultMessage += "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                        "Хотите сыграть ещё?";
                gameCallback.sendMessage(resultMessage, chatId, keyboardFactory.createGameSelectionKeyboard());
            } else {
                gameCallback.sendMessage("❌ Ошибка при выплате выигрыша", chatId, null);
            }
        } else {

            int newBalance = userService.getUserBalance(Long.valueOf(chatId));
            userService.changeLossesAndLostBlackJack(Long.valueOf(chatId), currentBet);
            String lossMessage = "❌ **Вы проиграли!**\n" +
                    "Потеряно: " + currentBet + " 🪙\n" +
                    "💰 Остаток баланса: " + newBalance + " 🪙\n\n" +
                    "Хотите сыграть ещё?";
            gameCallback.sendMessage(lossMessage, chatId, keyboardFactory.createGameSelectionKeyboard());
        }
        resetGame();
    }

    @Override
    public void processBet(String callbackData) {
        if (betPlaced) {
            return;
        }

        try {
            int bet = 0;
            if ("bet_all".equals(callbackData)) {
                bet = userService.getUserBalance(Long.valueOf(chatId));
            } else {
                bet = Integer.parseInt(callbackData.replace("bet_", ""));
            }

            if (bet <= 0) {
                gameCallback.sendMessage("❌ Ставка должна быть больше 0!", chatId, null);
                return;
            }

            if (userService.canPlaceBet(Long.valueOf(chatId), bet)) {
                if (userService.placeBet(Long.valueOf(chatId), bet)) {
                    currentBet = bet;
                    betPlaced = true;
                    int balance = userService.getUserBalance(Long.valueOf(chatId));
                    String confirmMessage = "✅ Ставка " + bet + " 🪙 принята!\n" +
                            "💰 Текущий баланс: " + balance + " 🪙\n\n" +
                            "Начинаем игру в Black Jack!";
                    gameCallback.sendMessage(confirmMessage, chatId, null);
                    dealInitialCards();
                    sendGameState();
                } else {
                    gameCallback.sendMessage("❌ Ошибка при размещении ставки", chatId, null);
                }
            } else {
                int balance = userService.getUserBalance(Long.valueOf(chatId));
                String insufficientFunds = "❌ Недостаточно средств для ставки " + bet + " 🪙\n" +
                        "💰 Ваш баланс: " + balance + " 🪙";
                gameCallback.sendMessage(insufficientFunds, chatId, keyboardFactory.createBetKeyboard());
            }
        } catch (NumberFormatException e) {
            gameCallback.sendMessage("❌ Неверный формат ставки", chatId, null);
        }
    }

    /**
     * Определение текстового результата игры (для отображения в sendGameState)
     */
    private String determineWinner() {
        if (playerScore > 21) {
            return "Вы **перебрали**! Проигрыш. 😞";
        } else if (dealerScore > 21) {
            return "Дилер **перебрал**! Вы выиграли! 🎉";
        } else if (playerScore > dealerScore) {
            return "Вы набрали **больше очков**! Победа! 🎉";
        } else if (dealerScore > playerScore) {
            return "Дилер набрал **больше очков**! Проигрыш. 😞";
        } else {
            return "**Ничья**! 🤝 (ставка возвращается)";
        }
    }
}
