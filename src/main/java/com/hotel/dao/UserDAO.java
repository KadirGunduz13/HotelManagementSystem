package com.hotel.dao;

import com.hotel.config.DatabaseConnection;
import com.hotel.models.Customer;
import com.hotel.models.Staff;
import com.hotel.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String identifier, String password) {
        String sql = "SELECT * FROM users WHERE (username = ? OR email = ? OR tc_no = ?) AND password = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            stmt.setString(3, identifier);
            stmt.setString(4, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    // phone ve email verilerini çekiyoruz
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Staff(
                                rs.getInt("id"), rs.getString("tc_no"), rs.getString("username"),
                                rs.getString("password"), rs.getString("full_name"),
                                rs.getString("phone"), rs.getString("email")
                        );
                    } else {
                        return new Customer(
                                rs.getInt("id"), rs.getString("tc_no"), rs.getString("username"),
                                rs.getString("password"), rs.getString("full_name"),
                                rs.getString("phone"), rs.getString("email")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Customer> searchCustomers(String searchText) {
        List<Customer> customers = new ArrayList<>();
        String sql;

        if (searchText == null || searchText.trim().isEmpty()) {
            // Arama kutusu boşsa hepsini getir
            sql = "SELECT * FROM users WHERE role = 'CUSTOMER'";
        } else {
            // Arama kutusu doluysa: İsim, TC, Telefon VEYA Kullanıcı Adı'na göre ara
            sql = "SELECT * FROM users WHERE role = 'CUSTOMER' AND (full_name LIKE ? OR tc_no LIKE ? OR phone LIKE ? OR username LIKE ?)";
        }

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchPattern = "%" + searchText + "%";

                // 4 parametreyi de aynı arama metniyle dolduruyoruz
                stmt.setString(1, searchPattern); // full_name için
                stmt.setString(2, searchPattern); // tc_no için
                stmt.setString(3, searchPattern); // phone için
                stmt.setString(4, searchPattern); // username için (YENİ EKLENEN)
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(new Customer(
                            rs.getInt("id"),
                            rs.getString("tc_no"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("phone"),
                            rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // GÜNCELLENMİŞ KAYIT METODU (Çoklu Kontrol)
    public boolean registerCustomer(String tcNo, String username, String password, String fullName, String phone, String email) {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        // 1. ADIM: Önce bu bilgilerle kayıtlı biri var mı kontrol et
        String checkSql = "SELECT count(*) FROM users WHERE tc_no = ? OR username = ? OR phone = ? OR email = ? OR full_name = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, tcNo);
            checkStmt.setString(2, username);
            checkStmt.setString(3, phone);
            checkStmt.setString(4, email);
            checkStmt.setString(5, fullName);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Eğer kayıt varsa işlemi durdur ve false dön (Kayıt başarısız)
                System.out.println("HATA: Girilen bilgilerden biri (TC, Tel, Email, Ad veya Kullanıcı Adı) sistemde zaten kayıtlı!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // 2. ADIM: Kimse yoksa kaydı yap
        String insertSql = "INSERT INTO users (tc_no, username, password, full_name, phone, email, role) VALUES (?, ?, ?, ?, ?, ?, 'CUSTOMER')";

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, tcNo);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, fullName);
            stmt.setString(5, phone);
            stmt.setString(6, email);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomerProfile(int id, String phone, String email, String password) {
        String sql = "UPDATE users SET phone = ?, email = ?, password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}