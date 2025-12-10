package com.hotel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // 1. ADIM: Tekil (static) örneği tutacak değişken
    // Volatile: Çoklu iş parçacığı (Thread) durumlarında belleği senkronize eder.
    private static volatile DatabaseConnection instance;
    private Connection connection;

    // Veritabanı Bilgileri (Kendi şifreni buraya yazmalısın)
    private final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private final String USERNAME = "root";
    private final String PASSWORD = ""; // MySQL kurulumunda şifre koyduysan buraya yaz!

    // 2. ADIM: Constructor (Yapıcı) private olmalı!
    // Böylece dışarıdan 'new DatabaseConnection()' denilerek yeni nesne üretilmesi engellenir.
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Veritabanı bağlantısı başarılı! (Singleton)");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Veritabanı bağlantısı hatası: " + e.getMessage());
        }
    }

    // 3. ADIM: Global erişim noktası
    // Eğer nesne yoksa oluşturur, varsa olanı gönderir.
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            // Thread-safe (İplik güvenliği) için senkronize blok
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // Bağlantı nesnesini dışarıya verir
    public Connection getConnection() {
        return connection;
    }
}