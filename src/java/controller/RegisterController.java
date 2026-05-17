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

        // === BƯỚC MỚI: Kiểm tra cấu trúc định dạng Email bằng Regex ===
        // Định dạng yêu cầu: phải có ký tự hợp lệ trước @, có tên miền và đuôi mở rộng (ví dụ: .com, .vn, .edu.vn,...)
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        
        if (email == null || !email.matches(emailRegex)) {
            // Nếu không đúng định dạng email, chặn lại và trả về trang đăng ký với mã lỗi invalidEmail
            response.sendRedirect("register.jsp?error=invalidEmail");
            return; // Dừng xử lý các bước tiếp theo
        }
        // ============================================================

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