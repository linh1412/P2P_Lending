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

        // 1. Kiểm tra dữ liệu rỗng đầu vào
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        String trimmedEmail = email.trim();

        // 2. BƯỚC MỚI: Kiểm tra tài khoản đã đăng ký hay chưa
        // Giả định bạn có hàm checkEmailExist trả về boolean trong UserDAO
        boolean isEmailExist = userDAO.checkEmailExist(trimmedEmail); 
        
        if (!isEmailExist) {
            // Trường hợp 1: Email chưa đăng ký hệ thống
            request.setAttribute("errorMessage", "Tài khoản chưa được đăng ký trong hệ thống!");
            request.setAttribute("oldEmail", email); 
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return; // Dừng xử lý tại đây
        }

        // 3. Thực hiện kiểm tra mật khẩu (Khi chắc chắn email đã tồn tại)
        String[] result = userDAO.loginCheck(trimmedEmail, password);

        if (result != null) {
            HttpSession session = request.getSession();
            long userId = Long.parseLong(result[0]); 
            String userEmail = result[1];
            String role = result[2]; // 'admin', 'borrower', hoặc 'investor'

            // Lưu thông tin người dùng vào Session
            session.setAttribute("userId", userId);
            session.setAttribute("email", userEmail);
            session.setAttribute("role", role);

            // Kiểm tra trạng thái hồ sơ eKYC
            boolean hasSubmittedEKYC = userDAO.checkUserEKYC(userId);

            // Luồng điều hướng phân quyền
            if ("admin".equals(role)) {
                response.sendRedirect("admin-dashboard.jsp");
            } 
            else if ("borrower".equals(role)) {
                if (hasSubmittedEKYC) {
                    response.sendRedirect("borrower_dashboard.jsp");
                } else {
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else if ("investor".equals(role)) {
                if (hasSubmittedEKYC) {
                    response.sendRedirect("investor_dashboard.jsp");
                } else {
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else {
                request.setAttribute("errorMessage", "Vai trò hệ thống của tài khoản không hợp lệ!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            
        } else {
            // Trường hợp 2: Có email nhưng sai mật khẩu
            request.setAttribute("errorMessage", "Mật khẩu không chính xác! Vui lòng thử lại.");
            request.setAttribute("oldEmail", email); // Giữ lại email người dùng đỡ mất công gõ lại
            
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}