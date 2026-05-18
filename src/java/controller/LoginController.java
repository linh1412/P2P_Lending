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
    
    private final UserDAO userDAO = new UserDAO();

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

        // 3. Thực hiện kiểm tra đăng nhập bằng mật khẩu
        String[] result = userDAO.loginCheck(trimmedEmail, password);

        if (result != null) {
            HttpSession session = request.getSession();
            long userId = Long.parseLong(result[0]); 
            String userEmail = result[1];
            String role = result[2]; // 'admin', 'borrower', hoặc 'investor'

            // Lưu thông tin cơ bản vào Session của phiên đăng nhập
            session.setAttribute("userId", userId);
            session.setAttribute("email", userEmail);
            session.setAttribute("role", role);

            // Lấy chuỗi trạng thái text cụ thể ('pending', 'verified', 'rejected') phục vụ hiển thị ở Dashboard
            String ekycStatus = userDAO.getEkycStatus(userId);
            session.setAttribute("verification_status", ekycStatus != null ? ekycStatus : "none");

            // [XỬ LÝ ĐỒNG BỘ LUỒNG MỚI]: Kiểm tra xem tài khoản đã thực hiện gửi đủ ảnh eKYC hay chưa
            boolean hasCompletedEkyc = userDAO.checkUserEKYC(userId);

            // 4. Luồng điều hướng phân quyền theo trạng thái Đăng nhập lần 1 và lần 2
            if ("admin".equals(role)) {
                response.sendRedirect("admin-dashboard.jsp");
            } 
            else if ("borrower".equals(role)) {
                if (hasCompletedEkyc) {
                    // LOGIN LẦN 2: Đã gửi đầy đủ hồ sơ ảnh CCCD trước đó -> Vào Dashboard chính thức
                    response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard");
                } else {
                    // LOGIN LẦN 1: Chưa làm eKYC hoặc hồ sơ bị 'rejected' bắt làm lại -> Ép sang trang gửi eKYC
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else if ("investor".equals(role)) {
                if (hasCompletedEkyc) {
                    // LOGIN LẦN 2: Đối với nhà đầu tư đã hoàn tất đẩy dữ liệu -> Chuyển đến URL dashboard tương ứng
                    response.sendRedirect("InvestorDashboardServlet?action=dashboard");
                } else {
                    // LOGIN LẦN 1: Ép sang trang tải tài liệu xác thực
                    response.sendRedirect("ekyc.jsp");
                }
            } 
            else {
                request.setAttribute("errorMessage", "Vai trò hệ thống của tài khoản không hợp lệ!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            
        } else {
            // Sai mật khẩu đăng nhập
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