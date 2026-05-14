package controller;

import dao.BorrowerDAO;
import model.LoanApplication;
import model.Borrower;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/BorrowerDashboardServlet")
public class BorrowerDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        // Lấy userId của người dùng đang đăng nhập từ session
        Long userId = (Long) session.getAttribute("userId");

        // Nếu chưa đăng nhập (session hết hạn hoặc chưa vào login), chuyển hướng về login
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Khởi tạo đối tượng DAO duy nhất để xử lý dữ liệu
            BorrowerDAO bDao = new BorrowerDAO();

            // 1. Lấy thông tin hồ sơ cá nhân (tên, thu nhập, trạng thái xác thực)
            Borrower borrower = bDao.getBorrowerById(userId);

            // 2. Lấy danh sách các đơn vay của người này (bao gồm đơn 23tr Linh vừa tạo)
            // Sử dụng hàm getLoansByBorrower mà mình đã thêm vào BorrowerDAO cho Linh
            List<LoanApplication> loanList = bDao.getLoansByBorrower(userId);

            // 3. Đẩy dữ liệu vào Request để trang JSP có thể đọc được bằng thẻ ${...}
            request.setAttribute("borrower", borrower);
            request.setAttribute("loanList", loanList);

            // Chuyển tiếp (forward) sang trang dashboard để hiển thị
            // Dùng forward giúp giữ lại dữ liệu trong request scope
            request.getRequestDispatcher("borrower_dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            // In lỗi ra console để debug nếu có sự cố kết nối database
            e.printStackTrace();
            // Trả về lỗi 500 nếu có vấn đề hệ thống
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi kết nối dữ liệu Dashboard.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Nếu người dùng gửi yêu cầu POST đến đây, ta cũng xử lý như GET
        doGet(request, response);
    }
}