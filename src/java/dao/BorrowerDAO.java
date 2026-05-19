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

    // 1. CẬP NHẬT: Lấy thông tin cơ bản VÀ số dư ví (wallet_balance) của Borrower dựa trên ID
    public Borrower getBorrowerById(long borrowerId) {
        String sql = "SELECT borrower_id, first_name, last_name, verification_status, monthly_income, wallet_balance " +
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
                    
                    // LƯU Ý: Đảm bảo class model.Borrower của bạn đã có thuộc tính wallet_balance (Double hoặc BigDecimal)
                    // Nếu dùng getter/setter khác tên, hãy đổi lại dòng dưới này cho khớp
                    b.setWalletBalance(rs.getDouble("wallet_balance")); 
                    
                    return b;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. CẬP NHẬT ĐÃ SỬA LỖI LOGIC: Tính tổng dư nợ thực tế từ các khoản vay gọi vốn thành công/đang chạy
    public double getCurrentDebt(long borrowerId) {
        double totalDebt = 0.0;
        // SỬA: Thay 'active' thành 'success' (hoặc 'disbursed') để khớp hoàn toàn với ENUM của bảng loans nhóm bạn
        String sql = "SELECT SUM(l.total_amount) FROM loans l " +
                     "INNER JOIN loan_applications la ON l.application_id = la.application_id " +
                     "WHERE la.borrower_id = ? AND l.status = 'success'";
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

    // 3. GIỮ NGUYÊN: Lấy danh sách hồ sơ đơn vay của Borrower (Sắp xếp đơn mới nhất lên đầu)
    public List<LoanApplication> getLoansByBorrower(long borrowerId) {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT application_id, amount_requested, term_months, created_at, cic_pdf_url, status " +
                     "FROM loan_applications WHERE borrower_id = ? ORDER BY created_at DESC";
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

    /**
     * 🛠️ GIỮ NGUYÊN: Cập nhật thông tin eKYC đầy đủ khi gửi lại hồ sơ bị lỗi
     */
    public boolean updateBorrowerEkyc(Borrower borrower) {
        String sql = "UPDATE borrowers SET first_name = ?, last_name = ?, monthly_income = ?, verification_status = ? WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, borrower.getFirstName());
            ps.setString(2, borrower.getLastName());
            ps.setDouble(3, borrower.getMonthlyIncome());
            ps.setString(4, borrower.getVerificationStatus());
            ps.setLong(5, borrower.getBorrowerId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * GIỮ NGUYÊN: Hàm cũ của bạn dùng để update nhanh trạng thái eKYC
     */
    public boolean updateEkycStatus(long borrowerId, String status) {
        String sql = "UPDATE borrowers SET verification_status = ? WHERE borrower_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setLong(2, borrowerId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * BỔ SUNG: Hàm trùng tên (Alias) với luồng gọi trong Controller nhằm tránh lỗi Compile Error
     */
    public boolean updateVerificationStatus(long borrowerId, String status) {
        return this.updateEkycStatus(borrowerId, status);
    }
}