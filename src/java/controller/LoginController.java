package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LoginController") 
public class LoginController extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String[] result = userDAO.loginCheck(email, password);

        if (result != null) {
            HttpSession session = request.getSession();
            long userId = Long.parseLong(result[0]); 
            String userEmail = result[1];
            String role = result[2];

            session.setAttribute("userId", userId);
            session.setAttribute("email", userEmail);
            session.setAttribute("role", role);

            // PHÂN QUYỀN VÀ PHÂN LUỒNG TRẠNG THÁI EKYC CHÍNH XÁC
            if ("admin".equals(role)) {
                response.sendRedirect("admin-dashboard.jsp");
            } 
            else if ("borrower".equals(role)) {
                // Kiểm tra xem Borrower đã nộp đủ hồ sơ ảnh giấy tờ chưa
                boolean hasSubmittedEKYC = userDAO.checkBorrowerEKYC(userId);
                
                if (hasSubmittedEKYC) {
                    // Đã nộp hồ sơ hợp lệ -> Vào thẳng borrower_dashboard.jsp
                    response.sendRedirect("borrower_dashboard.jsp");
                } else {
                    // Chưa gửi hồ sơ giấy tờ -> Chuyển đến trang đăng tải eKYC
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else if ("investor".equals(role)) {
                // Kiểm tra xem Investor đã nộp đủ hồ sơ ảnh giấy tờ chưa
                boolean hasSubmittedEKYC = userDAO.checkInvestorEKYC(userId);
                
                if (hasSubmittedEKYC) {
                    // Đã nộp hồ sơ hợp lệ -> Chuyển đến trang quản lý của Investor
                    response.sendRedirect("investor_dashboard.jsp");
                } else {
                    // Chưa gửi hồ sơ giấy tờ -> Chuyển đến trang đăng tải eKYC
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else {
                response.sendRedirect("login.jsp");
            }
            
        } else {
            // Chuyển hướng kèm tham số báo lỗi mật khẩu/email sai
            response.sendRedirect("login.jsp?error=invalid");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}