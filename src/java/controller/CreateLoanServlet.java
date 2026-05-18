package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import util.DBConnection;

@WebServlet("/CreateLoanServlet") // Đường dẫn gọi Servlet xử lý Form
public class CreateLoanServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Đảm bảo đọc tiếng Việt không bị lỗi font
        request.setCharacterEncoding("UTF-8");
        
        // 2. Lấy borrowerId (chính là userId đăng nhập) từ Session
        HttpSession session = request.getSession();
        Long borrowerId = (Long) session.getAttribute("userId");
        
        // Cơ chế dự phòng khi test nếu chưa làm xong chức năng đăng nhập
        if (borrowerId == null) {
            borrowerId = 1L; 
        }

        // 3. Đọc dữ liệu từ form gửi lên (Phải khớp chính xác với thuộc tính 'name' trong thẻ input của JSP)
        String amountStr = request.getParameter("amountRequested");
        String termStr = request.getParameter("termMonths");
        String cicPdfUrl = request.getParameter("cicPdfUrl");

        // 4. Ép kiểu dữ liệu an toàn
        double amountRequested = 0.0;
        int termMonths = 0;
        try {
            if (amountStr != null) amountRequested = Double.parseDouble(amountStr);
            if (termStr != null) termMonths = Integer.parseInt(termStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // 5. Câu lệnh SQL chèn vào bảng loan_applications của bạn
        String sql = "INSERT INTO loan_applications (borrower_id, amount_requested, term_months, status, cic_pdf_url, created_at) " +
                     "VALUES (?, ?, ?, 'pending', ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, borrowerId);
            ps.setDouble(2, amountRequested);
            ps.setInt(3, termMonths);
            ps.setString(4, cicPdfUrl);

            int rowsInserted = ps.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("🎉 Đã chèn thành công đơn vay vào database!");
                // Sau khi lưu thành công, đá trình duyệt quay về Dashboard hiển thị danh sách mới luôn
                response.sendRedirect("BorrowerDashboardServlet?action=dashboard");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi: Không thể lưu đơn vay.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Lỗi kết nối MySQL: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Nếu người dùng cố tình vào link này bằng phương thức GET, tự động chuyển về trang tạo đơn
        response.sendRedirect("BorrowerDashboardServlet?action=create_loan");
    }
}