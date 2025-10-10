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

            BotConfig config = new BotConfig(args);
            botsApi.registerBot(new TelegramBot(config.getUsername(), config.getToken()));

            System.out.println("EchoBot успешно запущен!");


        } catch (TelegramApiException e) {
            System.err.println("Ошибка при запуске бота: " + e.getMessage());
        }
    }

}