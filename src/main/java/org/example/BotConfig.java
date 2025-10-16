package org.example;

/**
 * класс для управления конфигурацией бота
 */
public class BotConfig {
    private final String token;
    private final String username;

    /**
     *
     * конструктор конфигурации устанавливающий параметры для имени и токена
     */
    public BotConfig(String[] args) {
        this.token = getBotToken(args);
        this.username = getBotUsername(args);
    }

    /**
     * получает токен по умолчанию
     */
    private String getBotToken(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        return System.getProperty("BOT_TOKEN", "8089912223:AAGIQdvDV0FBOCI_FRQB1Lp2Oj5gZX7SN6o");
    }

    /**
     * получает имя бота по умолчанию
     */
    private String getBotUsername(String[] args) {
        if (args.length > 1) {
            return args[1];
        }
        return System.getProperty("BOT_USERNAME", "OOPject_bot");
    }

    /**
     *возвращает токен бота
     */
    public String getToken() { return token; }

    /**
     *возвращает имя бота
     */
    public String getUsername() { return username; }
}
