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

            // PHÂN QUYỀN: Admin vào thẳng Dashboard, User/Investor đi làm eKYC
            if ("admin".equals(role)) {
                response.sendRedirect("admin-dashboard.jsp");
            } else {
                // Ép cả Borrower và Investor sang trang eKYC
                response.sendRedirect("ekyc.jsp");
            }
        } else {
            response.sendRedirect("login.jsp?error=invalid");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}