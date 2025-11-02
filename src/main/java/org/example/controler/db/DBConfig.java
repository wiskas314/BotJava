package org.example.controler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * класс отвечающий за инициализацию базы данных, создание необходимых таблиц и предоставление подключений к БД
 */
public class DBConfig {
    private static final String DB_URL="jdbc:sqlite:users.db";
    private static DBConfig instance;

    /**
     * конструктор
     */
    private DBConfig(){
        initializeDB();
    }

    /**
     *возвращает единственный экземпляр класса
     */
    public static DBConfig getInstance() {
        if (instance == null) {
            instance = new DBConfig();
        }
        return instance;
    }

    /**
     * инициализирует базу данных
     */
    private void initializeDB() {
        try (Connection conn = getConnection()) {
            var statmt = conn.createStatement();
            statmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    chat_id INTEGER PRIMARY KEY,
                    username TEXT NOT NULL,
                    balance INTEGER DEFAULT 1000,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);
            System.out.println("База данных успешно инициализирована");
        } catch (SQLException e) {
            System.err.println("Ошибка инициализации базы данных: " + e.getMessage());
        }
    }

    /**
     * создает и возвращает подключение к бд
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

}
