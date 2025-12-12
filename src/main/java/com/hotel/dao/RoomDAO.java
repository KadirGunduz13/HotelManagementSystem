package com.hotel.dao;

import com.hotel.config.DatabaseConnection;
import com.hotel.models.Room;
import com.hotel.services.RoomFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public java.util.List<com.hotel.models.Room> getAllRooms() {
        java.util.List<com.hotel.models.Room> rooms = new java.util.ArrayList<>();
        String sql = "SELECT * FROM rooms";

        try (java.sql.Connection conn = com.hotel.config.DatabaseConnection.getInstance().getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 1. Fabrika odayı üretir (Ama ID'si 0 olarak gelir)
                com.hotel.models.Room r = com.hotel.services.RoomFactory.createRoom(
                        rs.getString("type"),
                        rs.getInt("capacity"),
                        rs.getString("room_number"),
                        rs.getDouble("price_per_night"),
                        rs.getString("status")
                );

                // 2. KRİTİK DÜZELTME: Veritabanındaki GERÇEK ID'yi nesneye işliyoruz
                r.setId(rs.getInt("id"));

                rooms.add(r);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // updateRoomStatus metodu için de aynısı
    public void updateRoomStatus(int roomId, String newStatus) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Yeni Oda Ekle (CREATE)
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, capacity, price_per_night, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType()); // Abstract class metodu
            // Kapasite bilgisini nesnenin içindeki 'capacity' alanından almamız lazım.
            // Ancak Abstract Class'ta getter yoksa protected alana erişemeyebiliriz.
            // Bu yüzden Room sınıfına 'public int getCapacity()' eklememiz gerekebilir.
            // Şimdilik protected olduğu için aynı pakette değilsek sorun çıkarır.
            // ÇÖZÜM: Reflection veya basitçe Room sınıfına getter eklemek.
            // Biz Room sınıfına getter eklediğini varsayarak (aşağıda yaptıracağım):
            stmt.setInt(3, room.getCapacity());
            stmt.setDouble(4, room.getPrice());
            stmt.setString(5, "AVAILABLE"); // Yeni oda her zaman müsaittir

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Odayı Sil (DELETE)
    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}