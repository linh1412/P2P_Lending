package dao;

import model.User;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {

    // ==========================================
    // PHẦN 1: PHỤC VỤ CHO REGISTERCONTROLLER
    // ==========================================

    public boolean checkEmailExist(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String email, String password, String role, 
                                String firstName, String lastName, 
                                String idCardNumber, double monthlyIncome, 
                                String riskAppetite) {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psProfile = null;
        ResultSet rsKeys = null;

        String sqlUser = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
        String sqlBorrower = "INSERT INTO borrowers (borrower_id, first_name, last_name, id_card_number, monthly_income, wallet_balance, verification_status, risk_level) "
                           + "VALUES (?, ?, ?, ?, ?, 0.00, 'pending', 'Medium')";
        
        String sqlInvestor = "INSERT INTO investors (investor_id, first_name, last_name, wallet_balance, frozen_balance, risk_appetite, verification_status) "
                           + "VALUES (?, ?, ?, 0.00, 0.00, ?, 'pending')";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, email);
            psUser.setString(2, password);
            psUser.setString(3, role);
            psUser.executeUpdate();

            rsKeys = psUser.getGeneratedKeys();
            long generatedUserId = 0;
            if (rsKeys.next()) {
                generatedUserId = rsKeys.getLong(1);
            } else {
                throw new Exception("Không lấy được ID từ bảng users.");
            }

            if ("borrower".equals(role)) {
                psProfile = conn.prepareStatement(sqlBorrower);
                psProfile.setLong(1, generatedUserId);
                psProfile.setString(2, firstName);
                psProfile.setString(3, lastName);
                psProfile.setString(4, idCardNumber);
                psProfile.setDouble(5, monthlyIncome);
                psProfile.executeUpdate();
            } 
            else if ("investor".equals(role)) {
                psProfile = conn.prepareStatement(sqlInvestor);
                psProfile.setLong(1, generatedUserId);
                psProfile.setString(2, firstName);
                psProfile.setString(3, lastName);
                psProfile.setString(4, riskAppetite); 
                psProfile.executeUpdate();
            }

            conn.commit(); 
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            try { if (rsKeys != null) rsKeys.close(); } catch (Exception e) {}
            try { if (psUser != null) psUser.close(); } catch (Exception e) {}
            try { if (psProfile != null) psProfile.close(); } catch (Exception e) {}
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (Exception e) {}
        }
    }

    // ==========================================
    // PHẦN 2: PHỤC VỤ CHO LOGINCONTROLLER
    // ==========================================

    public User loginCheck(String email, String password) {
        String sql = "SELECT user_id, email, role FROM users WHERE email = ? AND password = ?";
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
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * TỐI ƯU LOGIC ĐĂNG NHẬP LẦN 2 (ĐỒNG BỘ THEO ENUM DATABASE):
     * Kiểm tra tài khoản đã gửi đủ 3 ảnh bắt buộc của eKYC chưa.
     * ĐÃ SỬA: Đổi 'selfie' thành 'other' để khớp chuẩn xác 100% với ENUM bảng documents.
     */
    public boolean checkUserEKYC(long userId) {
        String sqlCountDocs = "SELECT COUNT(DISTINCT document_type) FROM documents WHERE user_id = ? "
                            + "AND document_type IN ('id_card_front', 'id_card_back', 'other')";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psDocs = conn.prepareStatement(sqlCountDocs)) {
            
            psDocs.setLong(1, userId);
            try (ResultSet rs = psDocs.executeQuery()) {
                if (rs.next()) {
                    // Trả về true nếu đếm đủ 3 loại ảnh riêng biệt ('id_card_front', 'id_card_back', 'other')
                    return rs.getInt(1) >= 3; 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getEkycStatus(long userId) {
        String sqlRole = "SELECT role FROM users WHERE user_id = ?";
        String sqlBorrower = "SELECT verification_status FROM borrowers WHERE borrower_id = ?";
        String sqlInvestor = "SELECT verification_status FROM investors WHERE investor_id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            String role = "";
            try (PreparedStatement psRole = conn.prepareStatement(sqlRole)) {
                psRole.setLong(1, userId);
                try (ResultSet rsRole = psRole.executeQuery()) {
                    if (rsRole.next()) role = rsRole.getString("role");
                }
            }

            if ("borrower".equals(role)) {
                try (PreparedStatement psB = conn.prepareStatement(sqlBorrower)) {
                    psB.setLong(1, userId);
                    try (ResultSet rsB = psB.executeQuery()) {
                        if (rsB.next()) return rsB.getString("verification_status");
                    }
                }
            } else if ("investor".equals(role)) {
                try (PreparedStatement psI = conn.prepareStatement(sqlInvestor)) {
                    psI.setLong(1, userId);
                    try (ResultSet rsI = psI.executeQuery()) {
                        if (rsI.next()) return rsI.getString("verification_status");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "none";
    }

    // ==========================================
    // PHẦN 3: PHỤC VỤ CHO EKYCCONTROLLER / UPLOAD
    // ==========================================

    public boolean insertDocument(Long userId, String type, String url) {
        String sql = "INSERT INTO documents (user_id, document_type, file_url) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, type);
            ps.setString(3, url);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrInsertEkycStatus(long userId, String status) {
        String sqlRole = "SELECT role FROM users WHERE user_id = ?";
        String sqlBorrower = "UPDATE borrowers SET verification_status = ? WHERE borrower_id = ?";
        String sqlInvestor = "UPDATE investors SET verification_status = ? WHERE investor_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            String role = "";
            try (PreparedStatement psRole = conn.prepareStatement(sqlRole)) {
                psRole.setLong(1, userId);
                try (ResultSet rsRole = psRole.executeQuery()) {
                    if (rsRole.next()) role = rsRole.getString("role");
                }
            }

            if ("borrower".equals(role)) {
                try (PreparedStatement ps = conn.prepareStatement(sqlBorrower)) {
                    ps.setString(1, status);
                    ps.setLong(2, userId);
                    return ps.executeUpdate() > 0;
                }
            } else if ("investor".equals(role)) {
                try (PreparedStatement ps = conn.prepareStatement(sqlInvestor)) {
                    ps.setString(1, status);
                    ps.setLong(2, userId);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}