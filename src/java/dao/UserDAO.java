package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.User;
import util.DBConnection;

public class UserDAO {

    // 1. Hàm KIỂM TRA EMAIL
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // 2. Hàm Đăng ký
    public long registerUser(String email, String password) throws SQLException {
        String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, 'borrower')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, password);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        }
        return -1;
    }

    // 3. Hàm Đăng nhập
    public User checkLogin(String email, String password) throws SQLException {
        String sql = "SELECT user_id, email, role, created_at FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUser_id(rs.getLong("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setCreated_at(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    // 4. Hàm Cập nhật vai trò
    public void updateUserRole(long user_id, String role) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setLong(2, user_id);
            ps.executeUpdate();
        }
    }

    // 5. HÀM MỚI: Lấy thông tin User theo ID (Để hết lỗi ở RoleController)
    public User getUserById(long userId) throws SQLException {
        String sql = "SELECT user_id, email, role, created_at FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUser_id(rs.getLong("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setCreated_at(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        return null;
    }
}