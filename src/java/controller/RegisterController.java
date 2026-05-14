package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterController", urlPatterns = {"/RegisterController"})
public class RegisterController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // 1. Kiểm tra mật khẩu khớp nhau
        if (password == null || !password.equals(confirmPassword)) {
            response.sendRedirect("register.jsp?error=mismatch");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            
            // 2. Kiểm tra xem email đã có trong hệ thống chưa
            // Giả sử bạn đã có hàm isEmailExists trong UserDAO
            if (userDAO.isEmailExists(email)) {
                response.sendRedirect("register.jsp?error=exists");
                return;
            }

            // 3. Thực hiện đăng ký
            long user_id = userDAO.registerUser(email, password);

            if (user_id > 0) {
                // Đăng ký thành công -> Lưu session và chọn vai trò
                request.getSession().setAttribute("userId", user_id);
                response.sendRedirect("select_role.jsp");
            } else {
                // Lỗi không xác định khi lưu vào DB
                response.sendRedirect("register.jsp?error=server");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Lỗi hệ thống/kết nối DB
            response.sendRedirect("register.jsp?error=server");
        }
    }
}