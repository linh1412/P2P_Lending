package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.LoanApplication;
import util.DBConnection;

public class LoanDAO {
    
    public LoanDAO() {}

    // 1. Tạo đơn vay mới
    public boolean createLoan(LoanApplication loan) throws SQLException {
        String sql = "INSERT INTO loan_applications (borrower_id, amount_requested, term_months, cic_issued_date, cic_pdf_url) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, loan.getBorrowerId());
            ps.setDouble(2, loan.getAmountRequested());
            ps.setInt(3, loan.getTermMonths());
            ps.setDate(4, loan.getCicIssuedDate());
            ps.setString(5, loan.getCicPdfUrl());
            return ps.executeUpdate() > 0;
        }
    }

    // 2. Lấy danh sách đơn vay (Sửa để khớp với Dashboard)
    public List<LoanApplication> getLoansByBorrower(long borrowerId) throws SQLException {
        List<LoanApplication> list = new ArrayList<>();
        // Lấy thêm trường created_at để hiển thị ngày tạo đơn
        String sql = "SELECT * FROM loan_applications WHERE borrower_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanApplication l = new LoanApplication();
                    l.setApplicationId(rs.getLong("application_id"));
                    l.setBorrowerId(rs.getLong("borrower_id"));
                    l.setAmountRequested(rs.getDouble("amount_requested"));
                    l.setTermMonths(rs.getInt("term_months"));
                    l.setStatus(rs.getString("status"));
                    l.setCicPdfUrl(rs.getString("cic_pdf_url"));
                    // Đảm bảo class LoanApplication có trường createdAt kiểu Timestamp hoặc Date
                    l.setCreatedAt(rs.getTimestamp("created_at")); 
                    list.add(l);
                }
            }
        }
        return list;
    }
    
    public boolean isVerified(long borrowerId) throws SQLException {
        String sql = "SELECT verification_status FROM borrowers WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "verified".equalsIgnoreCase(rs.getString("verification_status"));
                }
            }
        }
        return false;
    }
}