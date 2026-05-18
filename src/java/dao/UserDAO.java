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

    // 1b. Hàm kiểm tra sự tồn tại của Email để phục vụ báo lỗi login chi tiết
    public boolean checkEmailExist(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Hàm xử lý Đăng ký tài khoản (Transaction an toàn dữ liệu)
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
            conn.setAutoCommit(false); 

            String sqlUser = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
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
                throw new Exception("Lỗi không lấy được ID người dùng mới.");
            }

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
                String sqlInvestor = "INSERT INTO investors (investor_id, first_name, last_name, risk_appetite, verification_status) VALUES (?, ?, ?, ?, 'pending')";
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

    // 5. OPTIMIZED: Hàm kiểm tra eKYC của Borrower (Gộp câu lệnh SQL tăng tốc độ truy vấn)
    public boolean checkBorrowerEKYC(long userId) {
        // Gộp chung kiểm tra trạng thái của người vay để giảm số lượng kết nối DB thừa
        String sql = "SELECT verification_status FROM borrowers WHERE borrower_id = ?";
        String sqlCheckDocs = "SELECT COUNT(DISTINCT document_type) FROM documents WHERE user_id = ? AND document_type IN ('id_card_front', 'id_card_back')";
        
        try (Connection conn = DBConnection.getConnection()) {
            // Bước A: Kiểm tra nhanh xem có bị Admin từ chối (rejected) hay không
            try (PreparedStatement psStatus = conn.prepareStatement(sql)) {
                psStatus.setLong(1, userId);
                try (ResultSet rsStatus = psStatus.executeQuery()) {
                    if (rsStatus.next()) {
                        String status = rsStatus.getString("verification_status");
                        if ("rejected".equals(status)) {
                            return false; // Bị từ chối thì ép buộc đá về trang ekyc.jsp ngay
                        }
                    }
                }
            }
            
            // Bước B: Nếu không dính líu rejected, đếm số lượng tài liệu đã nộp
            try (PreparedStatement psDocs = conn.prepareStatement(sqlCheckDocs)) {
                psDocs.setLong(1, userId);
                try (ResultSet rsDocs = psDocs.executeQuery()) {
                    if (rsDocs.next()) {
                        return rsDocs.getInt(1) >= 2; 
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 6. Hàm kiểm tra eKYC của Investor dựa trên số lượng ảnh mặt trước + mặt sau
    public boolean checkInvestorEKYC(long userId) {
        String sqlCheckDocs = "SELECT COUNT(DISTINCT document_type) FROM documents WHERE user_id = ? AND document_type IN ('id_card_front', 'id_card_back')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psDocs = conn.prepareStatement(sqlCheckDocs)) {
            psDocs.setLong(1, userId);
            try (ResultSet rsDocs = psDocs.executeQuery()) {
                if (rsDocs.next()) {
                    return rsDocs.getInt(1) >= 2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 7. Hàm tích hợp cũ phục vụ các bộ lọc Filter hoặc LoginController cũ nếu có tham chiếu
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

    // =========================================================================
    // CODE ĐỒNG BỘ: ĐƯA TRẠNG THÁI REJECTED GHI ĐÈ LÊN LẠI THÀNH PENDING
    // =========================================================================

    /**
     * 8. Hàm lấy chuỗi trạng thái eKYC cụ thể từ Database để kiểm tra hiển thị Dashboard
     */
    public String getEkycStatus(long userId) {
        String sqlFetchRole = "SELECT role FROM users WHERE user_id = ?";
        String sqlBorrower = "SELECT verification_status FROM borrowers WHERE borrower_id = ?";
        String sqlInvestor = "SELECT verification_status FROM investors WHERE investor_id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            String role = "";
            try (PreparedStatement psRole = conn.prepareStatement(sqlFetchRole)) {
                psRole.setLong(1, userId);
                try (ResultSet rsRole = psRole.executeQuery()) {
                    if (rsRole.next()) {
                        role = rsRole.getString("role");
                    }
                }
            }
            
            if ("borrower".equals(role)) {
                try (PreparedStatement psBorrower = conn.prepareStatement(sqlBorrower)) {
                    psBorrower.setLong(1, userId);
                    try (ResultSet rsB = psBorrower.executeQuery()) {
                        if (rsB.next()) return rsB.getString("verification_status");
                    }
                }
            } else if ("investor".equals(role)) {
                try (PreparedStatement psInvestor = conn.prepareStatement(sqlInvestor)) {
                    psInvestor.setLong(1, userId);
                    try (ResultSet rsI = psInvestor.executeQuery()) {
                        if (rsI.next()) return rsI.getString("verification_status");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 9. Hàm ghi đè trạng thái eKYC: Đẩy thẳng trạng thái SQL về 'pending' khi upload lại hồ sơ thành công
     */
    public boolean updateOrInsertEkycStatus(long userId, String status) {
        String sqlFetchRole = "SELECT role FROM users WHERE user_id = ?";
        
        String checkBorrower = "SELECT COUNT(*) FROM borrowers WHERE borrower_id = ?";
        String updateBorrower = "UPDATE borrowers SET verification_status = ? WHERE borrower_id = ?";
        String insertBorrower = "INSERT INTO borrowers (borrower_id, verification_status) VALUES (?, ?)";
        
        String checkInvestor = "SELECT COUNT(*) FROM investors WHERE investor_id = ?";
        String updateInvestor = "UPDATE investors SET verification_status = ? WHERE investor_id = ?";
        String insertInvestor = "INSERT INTO investors (investor_id, verification_status) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            String role = "";
            try (PreparedStatement psRole = conn.prepareStatement(sqlFetchRole)) {
                psRole.setLong(1, userId);
                try (ResultSet rsRole = psRole.executeQuery()) {
                    if (rsRole.next()) {
                        role = rsRole.getString("role");
                    }
                }
            }

            if ("borrower".equals(role)) {
                try (PreparedStatement psCheck = conn.prepareStatement(checkBorrower)) {
                    psCheck.setLong(1, userId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            // Thực hiện UPDATE trạng thái đè từ 'rejected' -> 'pending'
                            try (PreparedStatement psUpdate = conn.prepareStatement(updateBorrower)) {
                                psUpdate.setString(1, status);
                                psUpdate.setLong(2, userId);
                                return psUpdate.executeUpdate() > 0;
                            }
                        } else {
                            try (PreparedStatement psInsert = conn.prepareStatement(insertBorrower)) {
                                psInsert.setLong(1, userId);
                                psInsert.setString(2, status);
                                return psInsert.executeUpdate() > 0;
                            }
                        }
                    }
                }
            } 
            else if ("investor".equals(role)) {
                try (PreparedStatement psCheck = conn.prepareStatement(checkInvestor)) {
                    psCheck.setLong(1, userId);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            try (PreparedStatement psUpdate = conn.prepareStatement(updateInvestor)) {
                                psUpdate.setString(1, status);
                                psUpdate.setLong(2, userId);
                                return psUpdate.executeUpdate() > 0;
                            }
                        } else {
                            try (PreparedStatement psInsert = conn.prepareStatement(insertInvestor)) {
                                psInsert.setLong(1, userId);
                                psInsert.setString(2, status);
                                return psInsert.executeUpdate() > 0;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}