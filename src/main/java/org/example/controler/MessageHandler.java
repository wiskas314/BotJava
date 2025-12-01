package org.example.controler;


import org.apache.commons.lang3.StringUtils;
import org.example.controler.db.User;
import org.example.controler.db.UserService;

import java.util.List;

/**
 * Класс для обрабатывания входящих сообщений и генерации ответа
 */
public class MessageHandler {
    private final UserService userService;


    public MessageHandler() {
        this.userService = new UserService();
    }

    /**
     * Обрабатывает текст входящего сообщения и возвращает текстовый ответ.
     */
    public String handleMessage(String message, String userName,Long chatId) {
        if (StringUtils.isNotEmpty(message)) {
            switch (message) {
                case "/start":
                    return "Привет, " + userName + "! Я бот, готовый помочь скоротать время." +
                            "\nЧтобы узнать, что я умею, введи /help";
                case "/help":
                    return """
                Вот список доступных команд:
                /start - Начать общение с ботом
                /help - Получить список команд
                /play - Вызывает меню с выбором игр
                /balance - Показывает ваш баланс
                /statistic - Отображает вашу статистику
                /statistic_all - Отображает вашу общую статистику
                /top - Показывает топ 10 игроков
                /task_settings - Настройка ежедневных заданий""";
                case "/statistic_all":
                    int totalWins = userService.getBjWins(chatId) + userService.getRtbWins(chatId);
                    int totalLosses = userService.getBjLosses(chatId) + userService.getRtbLosses(chatId);
                    int totalEarned = userService.getBjEarned(chatId) + userService.getRtbEarned(chatId);
                    int totalLost = userService.getBjLost(chatId) + userService.getRtbLost(chatId);

                    return "Побед - поражений: " + totalWins + " - " + totalLosses +
                            "\nВыиграно - проиграно: " + totalEarned + " - " + totalLost;
                case "/top":
                    return getTopPlayersMessage(chatId);
                default:
                    return echoMessage(message);
            }
        } else {
            return "Ошибка обработки входных данных, проверьте что вы ввели текст!";
        }
    }
    /**
     * Генерирует сообщение с топом игроков
     */
    private String getTopPlayersMessage(Long chatId) {
        List<User> topPlayers = userService.getTopPlayersByEarned(10);
        int playerRank = userService.getPlayerRankByEarned(chatId);
        int playerEarned = userService.getUserEarned(chatId);

        StringBuilder message = new StringBuilder();
        message.append("🏆 **Топ 10 игроков по заработку** 🏆\n\n");

        if (topPlayers.isEmpty()) {
            message.append("Пока нет игроков с заработком 😔");
        } else {
            for (int i = 0; i < topPlayers.size(); i++) {
                User player = topPlayers.get(i);
                message.append(String.format(" %d. %s - %d 🪙\n",
                        i + 1,
                        player.getUsername(),
                        player.getEarned()));
            }
        }

        message.append("\n");
        message.append("📊 **Ваша позиция в рейтинге:**\n");
        if (playerRank > 0) {
            message.append(String.format("Место: %d\n", playerRank));
            message.append(String.format("Заработано: %d 🪙", playerEarned));
        } else {
            message.append("Вы еще не заработали ничего в играх 😔");
        }

        return message.toString();
    }


    /**
     * Генерируется эхо-сообщение пользователю
     */
    private String echoMessage(String messageText) {
        return "Вы написали: " + messageText;
    }

}
