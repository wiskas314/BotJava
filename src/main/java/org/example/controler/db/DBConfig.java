package org.example.controler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс отвечающий за инициализацию базы данных, создание необходимых таблиц и предоставление подключений к БД
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
     *Возвращает единственный экземпляр класса
     */
    protected static DBConfig getInstance() {
        if (instance == null) {
            instance = new DBConfig();
        }
        return instance;
    }

    /**
     * Инициализирует базу данных
     */
    private void initializeDB() {
        try (Connection conn = getConnection()) {
            var statmt = conn.createStatement();
            statmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    chat_id INTEGER PRIMARY KEY,
                    username TEXT NOT NULL,
                    balance INTEGER DEFAULT 1000,
                    
                    bjWins INTEGER DEFAULT 0,
                    bjLosses INTEGER DEFAULT 0,
                    bjEarned INTEGER DEFAULT 0,
                    bjLost INTEGER DEFAULT 0,
                    
                    rtbWins INTEGER DEFAULT 0,
                    rtbLosses INTEGER DEFAULT 0,
                    rtbEarned INTEGER DEFAULT 0,
                    rtbLost INTEGER DEFAULT 0,
                    
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
     * Создает и возвращает подключение к бд
     */
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

}
