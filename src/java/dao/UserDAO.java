package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {

    // 1. Hàm kiểm tra Email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT email FROM users WHERE email = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) return true; 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
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
                throw new Exception("Lỗi lấy ID.");
            }

            if ("borrower".equals(role)) {
                String sqlBorrower = "INSERT INTO borrowers (borrower_id, first_name, last_name, id_card_number, monthly_income) VALUES (?, ?, ?, ?, ?)";
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

            conn.commit(); 
            return true;
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (Exception ex) {} }
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT user_id, email, role FROM users WHERE email = ? AND password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{ String.valueOf(rs.getLong("user_id")), rs.getString("email"), rs.getString("role") };
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return null;
    }
}