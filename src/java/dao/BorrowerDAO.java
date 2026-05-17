package dao;

import util.DBConnection;
import model.Borrower;
import model.LoanApplication;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BorrowerDAO {

    // 1. Lấy thông tin cơ bản của Borrower dựa trên ID người dùng đăng nhập (chính là borrower_id)
    public Borrower getBorrowerById(long borrowerId) {
        String sql = "SELECT borrower_id, first_name, last_name, verification_status, monthly_income " +
                     "FROM borrowers WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Borrower b = new Borrower();
                    b.setBorrowerId(rs.getLong("borrower_id"));
                    b.setFirstName(rs.getString("first_name"));
                    b.setLastName(rs.getString("last_name"));
                    b.setVerificationStatus(rs.getString("verification_status"));
                    b.setMonthlyIncome(rs.getDouble("monthly_income"));
                    return b;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Tính tổng dư nợ thực tế từ các khoản vay đang hoạt động
    public double getCurrentDebt(long borrowerId) {
        double totalDebt = 0.0;
        String sql = "SELECT SUM(l.total_amount) FROM loans l " +
                     "INNER JOIN loan_applications la ON l.application_id = la.application_id " +
                     "WHERE la.borrower_id = ? AND l.status = 'active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalDebt = rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalDebt;
    }

    // 3. Lấy danh sách hồ sơ đơn vay của Borrower (Map chuẩn theo file LoanApplication.java)
    public List<LoanApplication> getLoansByBorrower(long borrowerId) {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT application_id, amount_requested, term_months, created_at, cic_pdf_url, status " +
                     "FROM loan_applications WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanApplication loan = new LoanApplication();
                    
                    loan.setApplicationId(rs.getLong("application_id")); 
                    loan.setAmountRequested(rs.getDouble("amount_requested")); 
                    loan.setTermMonths(rs.getInt("term_months")); 
                    loan.setCreatedAt(rs.getTimestamp("created_at"));
                    loan.setCicPdfUrl(rs.getString("cic_pdf_url")); 
                    loan.setStatus(rs.getString("status"));
                    
                    list.add(loan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}