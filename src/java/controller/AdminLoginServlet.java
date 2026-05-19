package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/AdminLoginServlet"})
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu người dùng cố tình truy cập link trực tiếp, đẩy về trang đăng nhập
        response.sendRedirect("admin_login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Nhận thông tin đăng nhập từ form gửi lên
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 2. Kiểm tra tài khoản admin cứng theo yêu cầu của nhóm
        if ("admin@gmail.com".equals(email) && "12345678".equals(password)) {
            
            // Đăng nhập thành công -> Khởi tạo session lưu trạng thái đăng nhập
            HttpSession session = request.getSession();
            session.setAttribute("adminEmail", email);
            session.setAttribute("role", "admin");

            // Điều hướng thẳng sang AdminDashboardServlet
            response.sendRedirect("AdminDashboardServlet");
            
        } else {
            // Đăng nhập thất bại -> Trả về thông báo lỗi và load lại trang đăng nhập admin
            request.setAttribute("errorMessage", "Tài khoản hoặc Mật khẩu quản trị viên không đúng!");
            request.getRequestDispatcher("admin_login.jsp").forward(request, response);
        }
    }
}