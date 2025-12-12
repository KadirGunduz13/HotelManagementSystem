package com.hotel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Veritabanı Bilgileri (Kendi şifrenle güncellemeyi unutma!)
    private final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private final String USER = "root";
    private final String PASSWORD = ""; // Buraya kendi şifreni yaz

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // KRİTİK DÜZELTME BURADA:
    public Connection getConnection() {
        try {
            // Eğer bağlantı kopmuşsa veya kapatılmışsa YENİDEN BAĞLAN
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}