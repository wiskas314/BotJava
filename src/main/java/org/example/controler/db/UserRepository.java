package org.example.controler.db;

import java.sql.SQLException;
import java.sql.*;


public class UserRepository {
    private final DBConfig dbConfig;

    public UserRepository(){
        this.dbConfig=DBConfig.getInstance();
    }
    /**
     * Создание или получение пользователя
     */
    public User createOrGetUser(Long chatId, String username) {
        String sql = "INSERT OR IGNORE INTO users (chat_id, username, balance) VALUES (?, ?, 1000)";

        try (Connection conn = dbConfig.conn;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setLong(1, chatId);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

            return getUserByChatId(chatId);

        } catch (SQLException e) {
            System.err.println("Ошибка создания пользователя: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }

    /**
     * Получение пользователя по chatId
     */
    public User getUserByChatId(Long chatId) {
        String sql = "SELECT chat_id, username, balance FROM users WHERE chat_id = ?";

        try (Connection conn = dbConfig.conn;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, chatId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getLong("chat_id"),
                        rs.getString("username"),
                        rs.getInt("balance")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения пользователя: " + e.getMessage());
        }

        return null;
    }

    /**
     * Обновление баланса пользователя
     */
    public boolean updateBalance(Long chatId, int newBalance) {
        String sql = "UPDATE users SET balance = ?, updated_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (Connection conn = dbConfig.conn;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newBalance);
            pstmt.setLong(2, chatId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка обновления баланса: " + e.getMessage());
            return false;
        }
    }

    /**
     * Изменение баланса на указанную сумму (положительную или отрицательную)
     */
    public boolean changeBalance(Long chatId, int amount) {
        String sql = "UPDATE users SET balance = balance + ?, updated_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (Connection conn = dbConfig.conn;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, amount);
            pstmt.setLong(2, chatId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка изменения баланса: " + e.getMessage());
            return false;
        }
    }

    /**
     * Проверка существования пользователя
     */
    public boolean userExists(Long chatId) {
        return getUserByChatId(chatId) != null;
    }

    /**
     * Проверка достаточности баланса
     */
    public boolean hasSufficientBalance(Long chatId, int requiredAmount) {
        User user = getUserByChatId(chatId);
        return user != null && user.getBalance() >= requiredAmount;
    }

}
