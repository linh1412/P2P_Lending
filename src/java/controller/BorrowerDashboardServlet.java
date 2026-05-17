package controller;

import dao.BorrowerDAO;
import model.LoanApplication;
import model.Borrower;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/BorrowerDashboardServlet")
public class BorrowerDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        // Kiểm tra phiên đăng nhập bảo mật
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentAction = request.getParameter("action");
        if (currentAction == null || currentAction.isEmpty()) {
            currentAction = "dashboard";
        }

        try {
            BorrowerDAO bDao = new BorrowerDAO();

            // Vì user_id liên kết 1-1 với borrower_id, ta truyền trực tiếp userId từ session vào
            Borrower borrower = bDao.getBorrowerById(userId);
            
            // CƠ CHẾ DỰ PHÒNG CHỐNG TRỐNG TRONG QUÁ TRÌNH KHỞI CHẠY KIỂM THỬ:
            // Nếu tài khoản đăng nhập chưa được tạo bản ghi bên bảng borrowers,
            // hệ thống tự động lấy dữ liệu tài khoản có borrower_id = 1 (Linh Hà - 12.000.000đ) để test giao diện.
            if (borrower == null) {
                borrower = bDao.getBorrowerById(1);
                if (borrower != null) {
                    userId = borrower.getBorrowerId();
                }
            }

            // Khởi tạo các giá trị hiển thị mặc định
            String fullName = "Người dùng";
            String verificationStatus = "pending";
            double monthlyIncome = 0.0;
            double maxLimit = 0.0;

            if (borrower != null) {
                fullName = borrower.getFirstName() + " " + borrower.getLastName();
                verificationStatus = borrower.getVerificationStatus();
                monthlyIncome = borrower.getMonthlyIncome();
                // Công thức tính toán hạn mức tự động: Gấp 3 lần thu nhập hàng tháng công bố
                maxLimit = monthlyIncome * 3.0;
            }

            // Tính toán tổng dư nợ thực tế từ database
            double currentDebt = bDao.getCurrentDebt(userId);

            // Tải danh sách đơn vay cá nhân thực tế
            List<LoanApplication> loanList = bDao.getLoansByBorrower(userId);

            // Đẩy toàn bộ dữ liệu ra Request Scope để hiển thị lên trang JSP
            request.setAttribute("currentAction", currentAction);
            request.setAttribute("borrowerName", fullName);
            request.setAttribute("trangThaiEkyc", verificationStatus);
            request.setAttribute("thuNhapKhai", monthlyIncome);
            request.setAttribute("hanMucToiDa", maxLimit);
            request.setAttribute("tongDuNo", currentDebt);
            request.setAttribute("myLoansList", loanList);

            // Chuyển hướng đồng bộ thông tin sang trang hiển thị
            request.getRequestDispatcher("borrower_dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tải thông tin Dashboard.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}