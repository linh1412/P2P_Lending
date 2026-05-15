package controller;

import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterController")
public class RegisterController extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        // 1. Kiểm tra email đã tồn tại trong hệ thống chưa
        if (userDAO.isEmailExists(email)) {
            response.sendRedirect("register.jsp?error=emailExists");
            return;
        }

        String idCardNumber = "";
        double monthlyIncome = 0.0;
        String riskAppetite = "";

        if ("borrower".equals(role)) {
            idCardNumber = request.getParameter("idCardNumber");
            String incomeStr = request.getParameter("monthlyIncome");
            if (incomeStr != null && !incomeStr.isEmpty()) {
                monthlyIncome = Double.parseDouble(incomeStr);
            }
        } else if ("investor".equals(role)) {
            riskAppetite = request.getParameter("riskAppetite");
        }

        // 2. Thực hiện lưu vào Database
        boolean isSuccess = userDAO.registerUser(
            email, password, role, firstName, lastName, 
            idCardNumber, monthlyIncome, riskAppetite
        );

        if (isSuccess) {
            // Đăng ký thành công -> sang trang Login
            response.sendRedirect("login.jsp?msg=success");
        } else {
            // Đăng ký thất bại (thường do trùng số CCCD hoặc lỗi kết nối)
            response.sendRedirect("register.jsp?error=dbError");
        }
    }
}