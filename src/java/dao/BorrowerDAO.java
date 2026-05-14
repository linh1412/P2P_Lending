package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Borrower;
import model.LoanApplication;
import util.DBConnection;

public class BorrowerDAO {

    // 1. Kiểm tra tồn tại (Dùng để quyết định Insert hay Update)
    public boolean isBorrowerExists(long userId) throws SQLException {
        String sql = "SELECT 1 FROM borrowers WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // 2. Cảnh báo trùng CCCD: Kiểm tra xem số CCCD đã có ai dùng chưa (loại trừ chính mình)
    public boolean isIdCardExists(String idCard, long currentBorrowerId) throws SQLException {
        String sql = "SELECT 1 FROM borrowers WHERE id_card_number = ? AND borrower_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idCard);
            ps.setLong(2, currentBorrowerId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // 3. Lấy dữ liệu: KHÔNG gọi cột id_image_url để tránh lỗi SQL
    public Borrower getBorrowerById(long userId) throws SQLException {
        String sql = "SELECT borrower_id, first_name, last_name, id_card_number, verification_status, monthly_income FROM borrowers WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Borrower b = new Borrower();
                    b.setBorrowerId(rs.getLong("borrower_id"));
                    b.setFirstName(rs.getString("first_name"));
                    b.setLastName(rs.getString("last_name"));
                    b.setIdCardNumber(rs.getString("id_card_number"));
                    b.setVerificationStatus(rs.getString("verification_status"));
                    b.setMonthlyIncome(rs.getDouble("monthly_income"));
                    return b;
                }
            }
        }
        return null;
    }

    // 4. Cập nhật hồ sơ: Đưa trạng thái về 'pending' để chờ duyệt
    public boolean updateProfile(Borrower b) throws SQLException {
        String sql = "INSERT INTO borrowers (borrower_id, first_name, last_name, id_card_number, monthly_income, verification_status) "
                   + "VALUES (?, ?, ?, ?, ?, 'pending') "
                   + "ON DUPLICATE KEY UPDATE first_name=?, last_name=?, id_card_number=?, monthly_income=?, verification_status='pending'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, b.getBorrowerId());
            ps.setString(2, b.getFirstName());
            ps.setString(3, b.getLastName());
            ps.setString(4, b.getIdCardNumber());
            ps.setDouble(5, b.getMonthlyIncome());
            ps.setString(6, b.getFirstName());
            ps.setString(7, b.getLastName());
            ps.setString(8, b.getIdCardNumber());
            ps.setDouble(9, b.getMonthlyIncome());
            return ps.executeUpdate() > 0;
        }
    }

    public List<LoanApplication> getLoansByBorrower(long userId) throws SQLException {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT * FROM loan_applications WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanApplication loan = new LoanApplication();
                    loan.setApplicationId(rs.getLong("application_id"));
                    loan.setAmountRequested(rs.getDouble("amount_requested"));
                    loan.setTermMonths(rs.getInt("term_months"));
                    loan.setStatus(rs.getString("status"));
                    list.add(loan);
                }
            }
        }
        return list;
    }
}