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

    // 1. Lấy thông tin cơ bản của Borrower dựa trên user_id đăng nhập
    public Borrower getBorrowerById(long userId) {
        String sql = "SELECT borrower_id, first_name, last_name, verification_status, monthly_income " +
                     "FROM borrowers WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, userId);
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

    // 2. KẾT HỢP CÁC BẢNG: Tính tổng dư nợ thực tế của các khoản vay đang hoạt động (active)
    public double getCurrentDebt(long userId) {
        double totalDebt = 0.0;
        String sql = "SELECT SUM(l.total_amount) FROM loans l " +
                     "INNER JOIN loan_applications la ON l.application_id = la.application_id " +
                     "INNER JOIN borrowers b ON la.borrower_id = b.borrower_id " +
                     "WHERE b.user_id = ? AND l.status = 'active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, userId);
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

    // 3. Lấy danh sách hồ sơ đơn vay của Borrower (Map chuẩn 100% theo file LoanApplication của bạn)
    public List<LoanApplication> getLoansByBorrower(long userId) {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT la.application_id, la.amount_requested, la.term_months, la.created_at, la.cic_pdf_url, la.status " +
                     "FROM loan_applications la " +
                     "INNER JOIN borrowers b ON la.borrower_id = b.borrower_id " +
                     "WHERE b.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanApplication loan = new LoanApplication();
                    
                    // Đã đồng bộ hoàn toàn với file Model của bạn:
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