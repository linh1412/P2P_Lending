package controller;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.checkLogin(email, password);

            if (user != null) {
                // 1. Đăng nhập thành công, tạo Session
                HttpSession session = request.getSession();
                
                // 2. Lưu các thông tin quan trọng vào Session để dùng ở các trang sau
                session.setAttribute("userId", user.getUser_id());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("userRole", user.getRole());

                // 3. ĐIỀU HƯỚNG TẬP TRUNG: 
                // Bất kể là ai, cứ đăng nhập xong là đẩy về select_role.jsp 
                // để họ tự chọn muốn làm Borrower hay Investor hôm nay.
                response.sendRedirect("select_role.jsp");
                
            } else {
                // Sai thông tin đăng nhập
                response.sendRedirect("login.jsp?error=invalid");
            }
        } catch (Exception e) {
            // In lỗi ra console của NetBeans để mình kiểm tra nếu có sự cố DB
            e.printStackTrace(); 
            response.sendRedirect("login.jsp?error=server");
        }
    }
}