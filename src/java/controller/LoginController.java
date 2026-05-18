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

        // 2. Kiểm tra tài khoản đã đăng ký hay chưa
        boolean isEmailExist = userDAO.checkEmailExist(trimmedEmail); 
        
        if (!isEmailExist) {
            request.setAttribute("errorMessage", "Tài khoản chưa được đăng ký trong hệ thống!");
            request.setAttribute("oldEmail", email); 
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return; 
        }

        // 3. Thực hiện kiểm tra đăng nhập
        String[] result = userDAO.loginCheck(trimmedEmail, password);

        if (result != null) {
            HttpSession session = request.getSession();
            long userId = Long.parseLong(result[0]); 
            String userEmail = result[1];
            String role = result[2]; // 'admin', 'borrower', hoặc 'investor'

            // Lưu thông tin cơ bản vào Session
            session.setAttribute("userId", userId);
            session.setAttribute("email", userEmail);
            session.setAttribute("role", role);

            // [CẬP NHẬT LUỒNG MỚI]: Lấy chuỗi trạng thái eKYC cụ thể từ Database
            // Giả định bạn bổ sung hàm getEkycStatus(userId) trả về String như: "verified", "pending", "rejected", hoặc null (chưa làm)
            String ekycStatus = userDAO.getEkycStatus(userId);
            
            // Đồng bộ trạng thái eKYC vào Session để trang Dashboard hoặc các trang khác kiểm tra trực tiếp
            session.setAttribute("verification_status", ekycStatus != null ? ekycStatus : "none");

            // Luồng điều hướng phân quyền theo trạng thái mới
            if ("admin".equals(role)) {
                response.sendRedirect("admin-dashboard.jsp");
            } 
            else if ("borrower".equals(role)) {
                // Nếu đã từng làm eKYC (bất kể trạng thái là verified, pending, hay thậm chí là rejected)
                if (ekycStatus != null && !ekycStatus.isEmpty()) {
                    // Cho phép quay về Dashboard để xem thông báo lỗi hoặc tiến độ
                    response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard");
                } else {
                    // Chưa từng khai báo thông tin eKYC lần nào -> ép ra trang đăng ký thông tin ban đầu
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else if ("investor".equals(role)) {
                if (ekycStatus != null && !ekycStatus.isEmpty()) {
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
            request.setAttribute("errorMessage", "Mật khẩu không chính xác! Vui lòng thử lại.");
            request.setAttribute("oldEmail", email); 
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}