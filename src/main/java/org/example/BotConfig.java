package org.example;

/**
 * Класс для управления конфигурацией бота
 */
public class BotConfig {
    private final String token;
    private final String username;

    /**
     * Конструктор конфигурации устанавливающий параметры для имени и токена
     */
    public BotConfig(String[] args) {
        this.token = getBotToken(args);
        this.username = getBotUsername(args);
    }

    /**
     * Получает токен по умолчанию
     */
    private String getBotToken(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        return System.getProperty("BOT_TOKEN", "8089912223:AAE2rXRpmFLRpF7ttdtr-2KmMS_2_a9RI2g");
    }

    /**
     * Получает имя бота по умолчанию
     */
    private String getBotUsername(String[] args) {
        if (args.length > 1) {
            return args[1];
        }
        return System.getProperty("BOT_USERNAME", "OOPject_bot");
    }

    /**
     *Возвращает токен бота
     */
    public String getToken() { return token; }

    /**
     *Возвращает имя бота
     */
    public String getUsername() { return username; }
}
