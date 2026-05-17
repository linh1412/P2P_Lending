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

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String[] result = userDAO.loginCheck(email.trim(), password);

        if (result != null) {
            HttpSession session = request.getSession();
            long userId = Long.parseLong(result[0]); 
            String userEmail = result[1];
            String role = result[2]; // 'admin', 'borrower', hoặc 'investor'

            // Lưu thông tin vào Session để các trang sau sử dụng
            session.setAttribute("userId", userId);
            session.setAttribute("email", userEmail);
            session.setAttribute("role", role);

            // Kiểm tra trạng thái hồ sơ eKYC trong cơ sở dữ liệu
            boolean hasSubmittedEKYC = userDAO.checkUserEKYC(userId);

            // LUỒNG PHÂN QUYỀN VÀ ĐIỀU HƯỚNG CHÍNH XÁC
            if ("admin".equals(role)) {
                response.sendRedirect("admin-dashboard.jsp");
            } 
            else if ("borrower".equals(role)) {
                if (hasSubmittedEKYC) {
                    // Lần đăng nhập thứ 2 trở đi (Đã có eKYC) -> Vào thẳng dashboard người vay
                    response.sendRedirect("borrower_dashboard.jsp");
                } else {
                    // Lần đầu đăng nhập (Chưa có eKYC) -> Bắt buộc sang trang ekyc.jsp
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else if ("investor".equals(role)) {
                if (hasSubmittedEKYC) {
                    // Lần đăng nhập thứ 2 trở đi (Đã có eKYC) -> Vào thẳng dashboard nhà đầu tư
                    response.sendRedirect("investor_dashboard.jsp");
                } else {
                    // Lần đầu đăng nhập (Chưa có eKYC) -> Bắt buộc sang trang ekyc.jsp
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else {
                response.sendRedirect("login.jsp");
            }
            
        } else {
            request.setAttribute("errorMessage", "Email hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}