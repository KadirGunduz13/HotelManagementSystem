package com.hotel.dao;

import com.hotel.config.DatabaseConnection;
import com.hotel.models.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // Tüm Rezervasyonları Listele (JOIN ile detaylı)
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        // SQL JOIN: Rezervasyonlar + Kullanıcılar + Odalar tablolarını birleştiriyoruz
        String sql = "SELECT r.*, u.full_name, rm.room_number " +
                "FROM reservations r " +
                "JOIN users u ON r.customer_id = u.id " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "ORDER BY r.created_at DESC";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                );

                // Ekstra bilgileri (Ad ve Oda No) nesneye ekliyoruz
                res.setCustomerName(rs.getString("full_name"));
                res.setRoomNumber(rs.getString("room_number"));

                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Durum Güncelleme (Check-In / Check-Out için)
    public boolean updateStatus(int id, String newStatus) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Yeni Rezervasyon Oluştur (Müşteri için)
    // Yeni Rezervasyon Oluştur (Müşteri için)
    public boolean createReservation(Reservation res) {
        String sql = "INSERT INTO reservations (customer_id, room_id, check_in_date, check_out_date, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, res.getCustomerId());
            stmt.setInt(2, res.getRoomId());
            stmt.setDate(3, res.getCheckInDate());
            stmt.setDate(4, res.getCheckOutDate());
            stmt.setDouble(5, res.getTotalPrice());
            stmt.setString(6, "PENDING");

            // DEĞİŞİKLİK BURADA:
            // Eskiden: return stmt.executeUpdate() > 0;
            // Şimdi: Sonucu bir değişkene alıyoruz, kontrol ediyoruz.

            int rowCount = stmt.executeUpdate(); // 1. Veritabanına kaydet

            if (rowCount > 0) {
                // 2. Kayıt başarılıysa OBSERVER devreye girsin
                try {
                    // Singleton üzerinden bildirim yöneticisini çağır
                    com.hotel.patterns.NotificationManager notifier = com.hotel.patterns.NotificationManager.getInstance();

                    // Gözlemciyi ekle (Demo olduğu için burada ekliyoruz)
                    notifier.addObserver(new com.hotel.patterns.ManagerSMSObserver());

                    // Bildirimi herkese yay
                    notifier.notifyAll("Yeni Rezervasyon! Oda: " + res.getRoomId() + " | Tutar: " + res.getTotalPrice() + " TL");

                } catch (Exception e) {
                    System.out.println("Bildirim gönderilemedi ama rezervasyon yapıldı: " + e.getMessage());
                }

                // 3. Artık gönül rahatlığıyla true dönebiliriz
                return true;
            } else {
                return false; // Kayıt yapılamadı
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Müşterinin kendi rezervasyonlarını getirmesi için
    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, rm.room_number FROM reservations r " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "WHERE r.customer_id = ? ORDER BY r.created_at DESC";

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                );
                res.setRoomNumber(rs.getString("room_number"));
                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Rezervasyon İptali (Transaction Kullanımı)
    public boolean cancelReservation(int reservationId, int roomId) {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try {
            // Otomatik kaydetmeyi kapat (Transaction Başlat)
            conn.setAutoCommit(false);

            // 1. Adım: Rezervasyon durumunu CANCELLED yap
            String sql1 = "UPDATE reservations SET status = 'CANCELLED' WHERE id = ?";
            try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                stmt1.setInt(1, reservationId);
                stmt1.executeUpdate();
            }

            // 2. Adım: Odayı tekrar MÜSAİT (AVAILABLE) yap
            String sql2 = "UPDATE rooms SET status = 'AVAILABLE' WHERE id = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                stmt2.setInt(1, roomId);
                stmt2.executeUpdate();
            }

            // Her şey yolundaysa kaydet (Commit)
            conn.commit();
            conn.setAutoCommit(true); // Eski haline getir
            return true;

        } catch (SQLException e) {
            // Hata varsa her şeyi geri al (Rollback)
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }
}