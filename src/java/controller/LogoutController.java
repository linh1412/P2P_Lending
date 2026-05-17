package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout") // Đường dẫn này phải khớp chính xác với thẻ href nút đăng xuất của bạn
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Lấy session hiện tại (nếu có)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.invalidate(); // Xóa toàn bộ dữ liệu phiên đăng nhập (userId, role...)
        }
        
        // Chuyển hướng người dùng về lại trang login
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}