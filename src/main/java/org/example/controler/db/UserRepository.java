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
    protected User createOrGetUser(Long chatId, String username) {
         String sql = """
          INSERT OR IGNORE INTO users (
            chat_id, 
            username, 
            balance, 
            bjWins, 
            bjLosses, 
            bjEarned, 
            bjLost,
            rtbWins, 
            rtbLosses, 
            rtbEarned, 
            rtbLost
          ) 
          VALUES (?, ?, 1000, 0, 0, 0, 0, 0, 0, 0, 0)
          """;

        try (Connection conn = dbConfig.getConnection();
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
    protected User getUserByChatId(Long chatId) {
         String sql = """
        SELECT chat_id, username, balance,
               bjWins, bjLosses, bjEarned, bjLost,
               rtbWins, rtbLosses, rtbEarned, rtbLost
        FROM users WHERE chat_id = ?
    """;

         try (Connection conn = dbConfig.getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setLong(1, chatId);
             ResultSet rs = pstmt.executeQuery();

             if (rs.next()) {
                 User user = new User(
                         rs.getLong("chat_id"),
                         rs.getString("username"),
                         rs.getInt("balance"),
                         rs.getInt("bjWins"),
                         rs.getInt("bjLosses"),
                         rs.getInt("bjEarned"),
                         rs.getInt("bjLost"),
                         rs.getInt("rtbWins"),
                         rs.getInt("rtbLosses"),
                         rs.getInt("rtbEarned"),
                         rs.getInt("rtbLost")
                 );
                 return user;
             }
         } catch (SQLException e) {
             System.err.println("Ошибка получения пользователя: " + e.getMessage());
         }
         return null;
     };


    /**
     * Изменение баланса на указанную сумму (положительную или отрицательную)
     */
    protected boolean changeBalance(Long chatId, int amount) {
        String sql = "UPDATE users SET balance = balance + ?, updated_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (Connection conn = dbConfig.getConnection();
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
     * Изменение кол-ва побед и общего выигрыша на указанную сумму Black Jack
     */
    protected boolean changeWinsAndEarnedBlackJack(Long chatId, int amount) {
        String sql = "UPDATE users SET bjWins = bjWins + 1, bjEarned = bjEarned + ?, updated_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (Connection conn = dbConfig.getConnection();
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
     * Изменение кол-ва поражений и общего проигрыша на указанную сумму Black Jack
     */
    protected boolean changeLossesAndLostBlackJack(Long chatId, int amount) {
        String sql = "UPDATE users SET bjLosses = bjLosses + 1, bjLost = bjLost + ?, updated_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (Connection conn = dbConfig.getConnection();
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
     * Изменение кол-ва побед и общего выигрыша на указанную сумму Ride the Bus
     */
    protected boolean changeWinAndEarnedRideTheBus(Long chatId, int amount) {
        String sql = "UPDATE users SET rtbWins = rtbWins + 1, rtbEarned = rtbEarned + ?, updated_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (Connection conn = dbConfig.getConnection();
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
     * Изменение кол-ва поражений и общего проигрыша на указанную сумму Ride the Bus
     */
    protected boolean changeLossesAndLostRideTheBus(Long chatId, int amount) {
        String sql = "UPDATE users SET rtbLosses = rtbLosses + 1, rtbLost = rtbLost + ?, updated_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (Connection conn = dbConfig.getConnection();
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
     * Проверка достаточности баланса
     */
    protected boolean hasSufficientBalance(Long chatId, int requiredAmount) {
        User user = getUserByChatId(chatId);
        return user != null && user.getBalance() >= requiredAmount;
    }

}
