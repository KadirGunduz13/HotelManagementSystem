package com.hotel.patterns;

import com.hotel.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseLoggerObserver implements IObserver {

    @Override
    public void update(String message) {
        String sql = "INSERT INTO logs (message) VALUES (?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, message);
            stmt.executeUpdate();

            System.out.println(">>> [OBSERVER - DB]: Log veritabanına kaydedildi.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}