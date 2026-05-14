package controller;

import dao.BorrowerDAO;
import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RoleController")
public class RoleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        // Lấy userId từ session (được set lúc đăng nhập thành công)
        Long userId = (Long) session.getAttribute("userId");
        // Lấy role người dùng vừa chọn từ giao diện (select_role.jsp)
        String role = request.getParameter("role");

        // 1. Kiểm tra đăng nhập: Nếu chưa có userId thì bắt quay lại login
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Cập nhật hoặc nạp đối tượng User vào session
        UserDAO userDAO = new UserDAO();
        try {
            // Nạp thông tin User đầy đủ để các trang Dashboard có dữ liệu dùng
            User user = userDAO.getUserById(userId);
            if (user != null) {
                // Nếu người dùng chọn role mới, cập nhật vào database và đối tượng
                if (role != null && !role.equals(user.getRole())) {
                    userDAO.updateUserRole(userId, role);
                    user.setRole(role);
                }
                session.setAttribute("user", user);
            } else {
                // Không tìm thấy user trong DB thì đẩy về login
                response.sendRedirect("login.jsp");
                return;
            }

            // 3. Điều hướng dựa trên Role
            if ("borrower".equals(role)) {
                BorrowerDAO bDao = new BorrowerDAO();
                // Kiểm tra xem đã có thông tin cá nhân trong bảng borrowers chưa
                if (bDao.isBorrowerExists(userId)) {
                    // Đã có hồ sơ -> Vào thẳng Dashboard
                    response.sendRedirect("borrower_dashboard.jsp");
                } else {
                    // Chưa có hồ sơ -> Yêu cầu cập nhật thông tin trước
                    response.sendRedirect("update_profile.jsp");
                }
            } else if ("investor".equals(role)) {
                // Nếu là nhà đầu tư, điều hướng sang dashboard tương ứng (Linh bổ sung sau nhé)
                response.sendRedirect("investor_dashboard.jsp");
            } else {
                // Nếu không chọn role hợp lệ
                response.sendRedirect("select_role.jsp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi kết nối cơ sở dữ liệu khi xử lý vai trò.");
        }
    }
}