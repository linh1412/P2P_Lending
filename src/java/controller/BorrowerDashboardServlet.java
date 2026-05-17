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

        // Lấy tham số tab hiện tại từ menu thanh điều hướng bên trái
        String currentAction = request.getParameter("action");
        if (currentAction == null || currentAction.isEmpty()) {
            currentAction = "dashboard";
        }

        try {
            BorrowerDAO bDao = new BorrowerDAO();

            // 1. Lấy thông tin tài khoản cá nhân
            Borrower borrower = bDao.getBorrowerById(userId);
            
            // Khởi tạo mặc định nếu database trống
            String fullName = "Người dùng";
            String verificationStatus = "pending";
            double monthlyIncome = 0.0;
            double maxLimit = 0.0;

            if (borrower != null) {
                fullName = borrower.getFirstName() + " " + borrower.getLastName();
                verificationStatus = borrower.getVerificationStatus();
                monthlyIncome = borrower.getMonthlyIncome();
                // Công thức tính toán hạn mức tự động: Gấp 3 lần thu nhập hàng tháng
                maxLimit = monthlyIncome * 3.0;
            }

            // 2. Tính toán tổng dư nợ thực tế
            double currentDebt = bDao.getCurrentDebt(userId);

            // 3. Tải danh sách đơn vay cá nhân
            List<LoanApplication> loanList = bDao.getLoansByBorrower(userId);

            // Đẩy dữ liệu vào Request scope trùng khớp 100% với các thẻ biến trên file JSP
            request.setAttribute("currentAction", currentAction);
            request.setAttribute("borrowerName", fullName);
            request.setAttribute("trangThaiEkyc", verificationStatus);
            request.setAttribute("thuNhapKhai", monthlyIncome);
            request.setAttribute("hanMucToiDa", maxLimit);
            request.setAttribute("tongDuNo", currentDebt);
            request.setAttribute("myLoansList", loanList);

            // Nếu nhóm có dữ liệu hiển thị toàn sàn, có thể nạp thêm marketLoansList tại đây

            // Chuyển tiếp (forward) đồng bộ sang trang hiển thị
            request.getRequestDispatcher("borrower_dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống khi đồng bộ Dashboard.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}