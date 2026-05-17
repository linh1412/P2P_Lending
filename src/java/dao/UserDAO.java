package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {

    // 1. Hàm kiểm tra Email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        String sql = "SELECT email FROM users WHERE email = ?";
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

    // 2. Hàm xử lý Đăng ký tài khoản (Transaction)
    public boolean registerUser(String email, String password, String role, 
                                String firstName, String lastName, 
                                String idCardNumber, double monthlyIncome, 
                                String riskAppetite) {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psProfile = null;
        ResultSet rsKeys = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Chèn vào bảng users
            String sqlUser = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
            psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, email);
            psUser.setString(2, password);
            psUser.setString(3, role);
            psUser.executeUpdate();

            // Lấy ID vừa tự động sinh ra
            rsKeys = psUser.getGeneratedKeys();
            long generatedUserId = 0;
            if (rsKeys.next()) {
                generatedUserId = rsKeys.getLong(1);
            } else {
                throw new Exception("Lỗi không lấy được ID người dùng mới.");
            }

            // Chèn vào bảng con tương ứng
            if ("borrower".equals(role)) {
                String sqlBorrower = "INSERT INTO borrowers (borrower_id, first_name, last_name, id_card_number, monthly_income, verification_status) VALUES (?, ?, ?, ?, ?, 'pending')";
                psProfile = conn.prepareStatement(sqlBorrower);
                psProfile.setLong(1, generatedUserId);
                psProfile.setString(2, firstName);
                psProfile.setString(3, lastName);
                psProfile.setString(4, idCardNumber);
                psProfile.setDouble(5, monthlyIncome);
                psProfile.executeUpdate();
            } else if ("investor".equals(role)) {
                String sqlInvestor = "INSERT INTO investors (investor_id, first_name, last_name, risk_appetite) VALUES (?, ?, ?, ?)";
                psProfile = conn.prepareStatement(sqlInvestor);
                psProfile.setLong(1, generatedUserId);
                psProfile.setString(2, firstName);
                psProfile.setString(3, lastName);
                psProfile.setString(4, riskAppetite);
                psProfile.executeUpdate();
            }

            conn.commit(); // Xác nhận Transaction thành công
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
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // 3. Hàm kiểm tra Đăng nhập
    public String[] loginCheck(String email, String password) {
        String sql = "SELECT user_id, email, role FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        String.valueOf(rs.getLong("user_id")), 
                        rs.getString("email"), 
                        rs.getString("role")
                    };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 4. Hàm hỗ trợ eKYC: Lưu đường dẫn ảnh vào bảng documents
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

    // 5. Kiểm tra xem Borrower đã thực sự hoàn thành tải giấy tờ eKYC lên chưa
    public boolean checkBorrowerEKYC(long userId) {
        String sqlCheckDocs = "SELECT COUNT(DISTINCT document_type) FROM documents WHERE user_id = ? AND document_type IN ('id_card_front', 'id_card_back')";
        String sqlCheckStatus = "SELECT verification_status FROM borrowers WHERE borrower_id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            
            // BƯỚC A: Kiểm tra xem trạng thái tài khoản có bị Admin Từ Chối (rejected) hay không
            try (PreparedStatement psStatus = conn.prepareStatement(sqlCheckStatus)) {
                psStatus.setLong(1, userId);
                try (ResultSet rsStatus = psStatus.executeQuery()) {
                    if (rsStatus.next()) {
                        String status = rsStatus.getString("verification_status");
                        if ("rejected".equals(status)) {
                            return false; 
                        }
                    }
                }
            }
            
            // BƯỚC B: Kiểm tra xem đã upload đủ 2 mặt CMND/CCCD chưa
            try (PreparedStatement psDocs = conn.prepareStatement(sqlCheckDocs)) {
                psDocs.setLong(1, userId);
                try (ResultSet rsDocs = psDocs.executeQuery()) {
                    if (rsDocs.next()) {
                        int docCount = rsDocs.getInt(1);
                        return docCount >= 2; 
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 6. Hàm MỚI: Kiểm tra xem Investor đã thực sự tải giấy tờ eKYC lên chưa
    public boolean checkInvestorEKYC(long userId) {
        String sqlCheckDocs = "SELECT COUNT(DISTINCT document_type) FROM documents WHERE user_id = ? AND document_type IN ('id_card_front', 'id_card_back')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psDocs = conn.prepareStatement(sqlCheckDocs)) {
            
            psDocs.setLong(1, userId);
            try (ResultSet rsDocs = psDocs.executeQuery()) {
                if (rsDocs.next()) {
                    int docCount = rsDocs.getInt(1);
                    return docCount >= 2; // Đã tải lên ít nhất cả mặt trước và mặt sau
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 7. Hàm tích hợp tổng hợp: Phục vụ trực tiếp luồng check eKYC của LoginController
    public boolean checkUserEKYC(long userId) {
        String sqlFetchRole = "SELECT role FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFetchRole)) {
            
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("borrower".equals(role)) {
                        return checkBorrowerEKYC(userId);
                    } else if ("investor".equals(role)) {
                        return checkInvestorEKYC(userId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}