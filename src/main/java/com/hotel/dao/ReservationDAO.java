package com.hotel.dao;

import com.hotel.config.DatabaseConnection;
import com.hotel.models.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean createReservation(Reservation res) {
        String sql = "INSERT INTO reservations (customer_id, room_id, check_in_date, check_out_date, total_price, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, res.getCustomerId());
            stmt.setInt(2, res.getRoomId());
            stmt.setDate(3, res.getCheckInDate());
            stmt.setDate(4, res.getCheckOutDate());
            stmt.setDouble(5, res.getTotalPrice());
            stmt.setString(6, "PENDING");

            int result = stmt.executeUpdate();

            if (result > 0) {
                // --- DÜZELTME BURADA ---
                // ID'leri kullanarak gerçek İsim ve Oda Numarasını buluyoruz
                String customerName = getCustomerNameById(res.getCustomerId());
                String roomNumber = getRoomNumberById(res.getRoomId());

                // Log Mesajını anlamlı hale getiriyoruz
                String logMessage = "Yeni Rezervasyon! Müşteri: " + customerName +
                        " | Oda: " + roomNumber +
                        " | Tutar: " + res.getTotalPrice() + " TL";

                // Observer'ı tetikle
                try {
                    // HATA ÇÖZÜMÜ: 'new' yerine 'getInstance()' kullanıyoruz
                    com.hotel.patterns.NotificationManager notifier = com.hotel.patterns.NotificationManager.getInstance();

                    // Listeyi temizle (Böylece aynı SMS 2 kere gitmez)
                    notifier.removeAllObservers();

                    // Gözlemcileri ekle
                    notifier.addObserver(new com.hotel.patterns.ManagerSMSObserver());
                    notifier.addObserver(new com.hotel.patterns.DatabaseLoggerObserver());

                    // Bildirimi Gönder
                    notifier.notifyAll(logMessage);

                } catch(Exception e) {
                    System.out.println("Bildirim hatası: " + e.getMessage());
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // YARDIMCI METOT 1: ID'den Müşteri İsmini Bulur
    private String getCustomerNameById(int id) {
        String sql = "SELECT full_name FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("full_name");
        } catch (SQLException e) {}
        return "Bilinmiyor";
    }

    // YARDIMCI METOT 2: ID'den Oda Numarasını Bulur
    private String getRoomNumberById(int id) {
        String sql = "SELECT room_number FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("room_number");
        } catch (SQLException e) {}
        return "Bilinmiyor";
    }

    // --- MEVCUT DİĞER METOTLAR (DEĞİŞMEDİ) ---

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name, rm.room_number FROM reservations r " +
                "JOIN users u ON r.customer_id = u.id " +
                "JOIN rooms rm ON r.room_id = rm.id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("room_id"),
                        rs.getDate("check_in_date"), rs.getDate("check_out_date"),
                        rs.getDouble("total_price"), rs.getString("status")
                );
                res.setCustomerName(rs.getString("full_name"));
                res.setRoomNumber(rs.getString("room_number"));
                list.add(res);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name, rm.room_number FROM reservations r " +
                "JOIN users u ON r.customer_id = u.id " +
                "JOIN rooms rm ON r.room_id = rm.id WHERE r.customer_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation res = new Reservation(
                            rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("room_id"),
                            rs.getDate("check_in_date"), rs.getDate("check_out_date"),
                            rs.getDouble("total_price"), rs.getString("status")
                    );
                    res.setCustomerName(rs.getString("full_name"));
                    res.setRoomNumber(rs.getString("room_number"));
                    list.add(res);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean cancelReservation(int resId, int roomId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement s1 = conn.prepareStatement("UPDATE reservations SET status='CANCELLED' WHERE id=?");
                 PreparedStatement s2 = conn.prepareStatement("UPDATE rooms SET status='AVAILABLE' WHERE id=?")) {
                s1.setInt(1, resId); s1.executeUpdate();
                s2.setInt(1, roomId); s2.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            } finally { conn.setAutoCommit(true); }
        } catch (SQLException e) { return false; }
    }
}