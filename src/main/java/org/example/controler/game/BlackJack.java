package org.example.controler.game;

import org.example.controler.keyboard.KeyboardBuilder;
import org.example.controler.keyboard.ButtonData;
import org.example.controler.game.cards.Card;
import org.example.controler.game.cards.Deck;
import org.example.controler.db.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, реализующий игру Black Jack
 */
public class BlackJack implements Game {
    private Deck deck;
    private final KeyboardBuilder keyboardBuilder;
    private String chatId;
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
        keyboardBuilder = new KeyboardBuilder();
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

    @Override
    public GameResponse startGame(String chatId) {
        this.chatId = chatId;

        if (!betPlaced) {
            int balance = userService.getUserBalance(Long.valueOf(chatId));
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
                                "Ваш баланс: " + balance + " 🪙\nСделайте ставку для начала игры",
                                keyboardBuilder.createBetKeyboard()
                        ),
                        false
                );
            }
        } else {
            dealInitialCards();
            return getGameResponse();
        }
    }
    /**
     * Получить текстовое представление состояния игры
     */
    private String getGameStateText() {
        StringBuilder gameState = new StringBuilder();
        gameState.append("🃏 **Black Jack** 🃏\n\n");
        gameState.append("Ставка: ").append(currentBet).append(" 🪙\n\n");
        gameState.append("Ваши карты:  ").append(getHandAsString(playerHand, true)).append("  (Сумма: ").append(playerScore).append(")\n");

        boolean showDealerAll = !isPlayerTurn || isGameOver;
        gameState.append("Карты дилера:  ").append(getHandAsString(dealerHand, showDealerAll));

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
            gameState.append("  (Сумма: ").append(visibleDealerScore).append(")\n\n");
        } else {
            gameState.append("  (Сумма: ").append(dealerScore).append(")\n\n");
        }

        if (!isGameOver) {
            if (isPlayerTurn) {
                gameState.append("Ваш ход! Хотите взять ещё карту?");
            } else {
                gameState.append("Ход дилера...");
            }
        }

        return gameState.toString();
    }
    /**
     * Получить GameResponse для текущего состояния игры
     */
    private GameResponse getGameResponse() {
        String gameStateText = getGameStateText();

        if (isGameOver) {
            return new GameResponse(
                    new GameMessage(chatId, gameStateText, keyboardBuilder.createGameSelectionButtons()),
                    true
            );
        } else {
            return new GameResponse(
                    new GameMessage(chatId, gameStateText, createHitOrStandKeyboard()),
                    false
            );
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



    @Override
    public GameResponse processUserChoice(String callbackData) {
        if (isGameOver) {
            return new GameResponse(
                    new GameMessage(chatId, "Игра уже завершена", null),
                    true
            );
        }

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
                return handleGameOver(false);
            } else {
                return getGameResponse();
            }
        } else if ("stand".equals(callbackData)) {
            isPlayerTurn = false;
            return dealerTurn();
        } else if ("exit".equals(callbackData)) {
            return handleEarlyExit();
        }

        return new GameResponse(
                new GameMessage(chatId, "Неизвестная команда", null),
                false
        );
    }

    /**
     * Ход дилера (автоматический)
     */
    private GameResponse dealerTurn() {
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
        return handleGameOver(determineIfPlayerWon());
    }
    /**
     * Обработка досрочного выхода
     */
    private GameResponse handleEarlyExit() {
        int returnAmount = currentBet;
        boolean success = userService.payWinnings(Long.valueOf(chatId), returnAmount);

        if (success) {
            resetGame();
            return new GameResponse(
                    new GameMessage(
                            chatId,
                            "🚪 Вы вышли из игры досрочно.\n" +
                                    "💰 Возвращено: " + returnAmount + " 🪙\n" +
                                    "Хотите сыграть ещё?",
                            keyboardBuilder.createGameSelectionButtons()
                    ),
                    true
            );
        } else {
            resetGame();
            return new GameResponse(
                    new GameMessage(chatId, "❌ Ошибка при возврате ставки", null),
                    true
            );
        }
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
     * Обработка конца игры
     */
    private GameResponse handleGameOver(boolean isWinner) {
        isGameOver = true;
        int winAmount = 0;
        String resultMessage;

        if (isWinner) {
            if (playerScore == 21 && playerHand[2] == null) {
                winAmount = (int) Math.floor(currentBet * 2.5);
                userService.changeWinsAndEarnedBlackJack(Long.valueOf(chatId), (int) Math.floor(currentBet * 1.5));
                userService.changeEarned(Long.valueOf(chatId), (int) Math.floor(currentBet * 1.5));
                resultMessage = "🎉 **Блэкджек!** Вы выиграли с натуральной 21!\n" +
                        "💎 Выигрыш: " + winAmount + " 🪙\n";
            } else if (playerScore == dealerScore) {
                winAmount = currentBet;
                userService.changeWinsAndEarnedBlackJack(Long.valueOf(chatId), 0);
                resultMessage = "🤝 **Ничья!** Ставка возвращается.\n" +
                        "💎 Возврат: " + winAmount + " 🪙\n";
            } else {
                // Обычная победа
                winAmount = currentBet * 2;
                userService.changeWinsAndEarnedBlackJack(Long.valueOf(chatId), currentBet);
                userService.changeEarned(Long.valueOf(chatId), currentBet);
                resultMessage = "🎉 **Вы выиграли!**\n" +
                        "💎 Выигрыш: " + winAmount + " 🪙\n";
            }

            boolean success = userService.payWinnings(Long.valueOf(chatId), winAmount);
            if (success) {
                int newBalance = userService.getUserBalance(Long.valueOf(chatId));
                resultMessage += "💰 Новый баланс: " + newBalance + " 🪙\n\n" +
                        "Хотите сыграть ещё?";
            } else {
                resultMessage = "❌ Ошибка при выплате выигрыша";
            }
        } else {
            int newBalance = userService.getUserBalance(Long.valueOf(chatId));
            userService.changeLossesAndLostBlackJack(Long.valueOf(chatId), currentBet);
            resultMessage = "❌ **Вы проиграли!**\n" +
                    "Потеряно: " + currentBet + " 🪙\n" +
                    "💰 Остаток баланса: " + newBalance + " 🪙\n\n" +
                    "Хотите сыграть ещё?";
        }

        resetGame();
        return new GameResponse(
                new GameMessage(chatId, getGameStateText() + "\n" + resultMessage,
                        keyboardBuilder.createGameSelectionButtons()),
                true
        );
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
            if ("bet_all".equals(callbackData)) {
                bet = userService.getUserBalance(Long.valueOf(chatId));
            } else {
                bet = Integer.parseInt(callbackData.replace("bet_", ""));
            }

            if (bet <= 0) {
                return new GameResponse(
                        new GameMessage(chatId, "❌ Ставка должна быть больше 0!", null),
                        false
                );
            }

            if (userService.canPlaceBet(Long.valueOf(chatId), bet)) {
                if (userService.placeBet(Long.valueOf(chatId), bet)) {
                    currentBet = bet;
                    betPlaced = true;

                    dealInitialCards();
                    return getGameResponse();
                } else {
                    return new GameResponse(
                            new GameMessage(chatId, "❌ Ошибка при размещении ставки", null),
                            false
                    );
                }
            } else {
                int balance = userService.getUserBalance(Long.valueOf(chatId));
                String insufficientFunds = "❌ Недостаточно средств для ставки " + bet + " 🪙\n" +
                        "💰 Ваш баланс: " + balance + " 🪙";
                return new GameResponse(
                        new GameMessage(chatId, insufficientFunds, keyboardBuilder.createBetKeyboard()),
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
     * Создает клавиатуру Hit/Stand для игры
     */
    public List<List<ButtonData>> createHitOrStandKeyboard() {
        List<List<ButtonData>> buttonRows = new ArrayList<>();

        List<ButtonData> row = new ArrayList<>();
        row.add(new ButtonData("➕ Взять карту", "hit"));
        row.add(new ButtonData("⛔ Остановиться", "stand"));
        row.add(new ButtonData("🚪 Выйти", "exit"));

        buttonRows.add(row);

        return buttonRows;
    }
}
