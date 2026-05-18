package dao; 

import util.DBConnection; 
import model.LoanApplication; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

    // =========================================================================
    // PHÂN HỆ NGƯỜI ĐI VAY (BORROWER)
    // =========================================================================

    /**
     Big SỬA ĐỔI/BỔ SUNG: Hàm nhận đối tượng LoanApplication để đồng bộ với Servlet
     */
    public boolean insertLoanApplication(LoanApplication loan) {
        String sql = "INSERT INTO loan_applications (borrower_id, amount_requested, term_months, status, cic_issued_date, cic_pdf_url, created_at) " +
                     "VALUES (?, ?, ?, 'pending', ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, loan.getBorrowerId());
            ps.setDouble(2, loan.getAmountRequested());
            ps.setInt(3, loan.getTermMonths());
            ps.setDate(4, loan.getCicIssuedDate()); 
            ps.setString(5, loan.getCicPdfUrl());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 1. Thêm đơn đăng ký vay mới (Hàm cũ của bạn - giữ lại để tránh lỗi nơi khác)
     */
    public boolean insertLoanApplication(long borrowerId, double amountRequested, int termMonths, Date cicIssuedDate, String cicPdfUrl) {
        String sql = "INSERT INTO loan_applications (borrower_id, amount_requested, term_months, status, cic_issued_date, cic_pdf_url, created_at) " +
                     "VALUES (?, ?, ?, 'pending', ?, ?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, borrowerId);
            ps.setDouble(2, amountRequested);
            ps.setInt(3, termMonths);
            ps.setDate(4, cicIssuedDate); 
            ps.setString(5, cicPdfUrl);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 2. Lấy danh sách đơn vay cá nhân để hiển thị ở bảng "Hồ Sơ Giao Dịch Khoản Vay Của Bạn"
     */
    public List<LoanApplication> getLoansByBorrower(long borrowerId) {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT application_id, borrower_id, amount_requested, term_months, status, cic_issued_date, cic_pdf_url, created_at " +
                     "FROM loan_applications WHERE borrower_id = ? ORDER BY application_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, borrowerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoanApplication loan = new LoanApplication();
                    loan.setApplicationId(rs.getLong("application_id"));
                    loan.setBorrowerId(rs.getLong("borrower_id"));
                    loan.setAmountRequested(rs.getDouble("amount_requested"));
                    loan.setTermMonths(rs.getInt("term_months"));
                    loan.setStatus(rs.getString("status"));
                    loan.setCicIssuedDate(rs.getDate("cic_issued_date"));
                    loan.setCicPdfUrl(rs.getString("cic_pdf_url"));
                    loan.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(loan);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 3. Lấy danh sách khoản vay hiển thị công khai trên "Khoản Vay Trên Sàn"
     * CHỈ lấy những đơn có trạng thái 'approved'
     */
    public List<LoanApplication> getAllMarketLoans() {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT application_id, borrower_id, amount_requested, term_months, status, cic_issued_date, cic_pdf_url, created_at " +
                     "FROM loan_applications WHERE status = 'approved' ORDER BY application_id DESC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LoanApplication loan = new LoanApplication();
                loan.setApplicationId(rs.getLong("application_id"));
                loan.setBorrowerId(rs.getLong("borrower_id"));
                loan.setAmountRequested(rs.getDouble("amount_requested"));
                loan.setTermMonths(rs.getInt("term_months"));
                loan.setStatus(rs.getString("status"));
                loan.setCicIssuedDate(rs.getDate("cic_issued_date"));
                loan.setCicPdfUrl(rs.getString("cic_pdf_url"));
                loan.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(loan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================================
    // PHÂN HỆ ADMIN (BAN QUẢN TRỊ KIỂM DUYỆT)
    // =========================================================================

    /**
     * 4. Lấy toàn bộ danh sách đơn vay đang chờ Admin duyệt (status = 'pending')
     */
    public List<LoanApplication> getPendingLoans() {
        List<LoanApplication> list = new ArrayList<>();
        String sql = "SELECT application_id, borrower_id, amount_requested, term_months, status, cic_issued_date, cic_pdf_url, created_at " +
                     "FROM loan_applications WHERE status = 'pending' ORDER BY created_at ASC"; 
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                LoanApplication loan = new LoanApplication();
                loan.setApplicationId(rs.getLong("application_id"));
                loan.setBorrowerId(rs.getLong("borrower_id"));
                loan.setAmountRequested(rs.getDouble("amount_requested"));
                loan.setTermMonths(rs.getInt("term_months"));
                loan.setStatus(rs.getString("status"));
                loan.setCicIssuedDate(rs.getDate("cic_issued_date"));
                loan.setCicPdfUrl(rs.getString("cic_pdf_url"));
                loan.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(loan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 5. Admin cập nhật trạng thái đơn vay ('approved' hoặc 'rejected')
     */
    public boolean updateLoanStatus(long applicationId, String status) {
        String sql = "UPDATE loan_applications SET status = ? WHERE application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setLong(2, applicationId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}