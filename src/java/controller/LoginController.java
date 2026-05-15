package controller; // Thuộc package controller của dự án P2P_Lending

import dao.UserDAO; // Import lớp UserDAO từ package dao sang để sử dụng
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Định tuyến đường dẫn xử lý hành động từ Form đăng nhập gửi lên
@WebServlet("/LoginServlet")
public class LoginController extends HttpServlet {
    
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Cấu hình UTF-8 đề phòng trường hợp lỗi font hệ thống
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 1. Lấy thông tin tài khoản từ Form đăng nhập (login.jsp) gửi lên
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 2. Gọi tầng DAO để thực hiện kiểm tra so khớp dưới Database
        String[] result = userDAO.loginCheck(email, password);

        // 3. Xử lý kết quả trả về từ Database
        if (result != null) {
            // ĐĂNG NHẬP THÀNH CÔNG: Tạo một Session để lưu trữ phiên làm việc của người dùng
            HttpSession session = request.getSession();
            
            long userId = Long.parseLong(result[0]); // Ép chuỗi user_id về kiểu số BIGINT (Java Long)
            String userEmail = result[1];
            String role = result[2]; // Giá trị ENUM lấy từ DB: 'admin', 'investor', hoặc 'borrower'

            // Lưu các thông tin cốt lõi vào Session để các trang sau kiểm tra phân quyền
            session.setAttribute("userId", userId);
            session.setAttribute("email", userEmail);
            session.setAttribute("role", role);

            System.out.println("--- Đăng nhập thành công ---");
            System.out.println("User ID: " + userId + " | Role: " + role);

            // 4. PHÂN QUYỀN ĐIỀU HƯỚNG: Dựa vào role trong DB để đưa về đúng giao diện Dashboard
            if ("admin".equals(role)) {
                response.sendRedirect("admin-dashboard.jsp");
            } else if ("borrower".equals(role)) {
                response.sendRedirect("borrower-dashboard.jsp");
            } else if ("investor".equals(role)) {
                response.sendRedirect("investor-dashboard.jsp");
            }
        } else {
            // ĐĂNG NHẬP THẤT BẠI: Quay lại trang login và đính kèm cờ báo lỗi lên thanh URL
            response.sendRedirect("login.jsp?error=invalid");
        }
    }
}