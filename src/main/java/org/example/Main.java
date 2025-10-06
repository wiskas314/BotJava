package org.example;



import org.example.controler.TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Main главный класс приложения для запуска Telegram бота
 */
public class Main {
    public static void main(String[] args) {
        try {

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);


            String botToken = getBotToken(args);
            String botUsername = getBotUsername(args);


            botsApi.registerBot(new TelegramBot(botUsername, botToken));

            System.out.println("EchoBot успешно запущен!");


        } catch (TelegramApiException e) {
            System.err.println("Ошибка при запуске бота: " + e.getMessage());
        }
    }

    private static String getBotToken(String[] args) {
        if (args.length > 0) {
            return args[0];
        }

        return System.getProperty("BOT_TOKEN", "token");
    }

    private static String getBotUsername(String[] args) {
        if (args.length > 1) {
            return args[1];
        }

        return System.getProperty("BOT_USERNAME", "username");
    }
}