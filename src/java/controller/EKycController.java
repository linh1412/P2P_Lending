package controller;

import dao.UserDAO;
import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; // KHẮC PHỤC LỖI: Đổi chính xác thành gói .http.Part để hết gạch đỏ

@WebServlet("/EKycController")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class EKycController extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId"); 
        String role = (String) session.getAttribute("role"); // Lấy vai trò từ Session lúc đăng nhập
        
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Cấu hình thư mục lưu ảnh vật lý trên máy chủ
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            Part frontPart = request.getPart("frontImg");
            Part backPart = request.getPart("backImg");
            Part facePart = request.getPart("faceImg");

            // Kiểm tra tính hợp lệ (Tránh gửi form trống ảnh)
            if (isEmpty(frontPart) || isEmpty(backPart) || isEmpty(facePart)) {
                response.sendRedirect("ekyc.jsp?error=missingFiles");
                return;
            }

            // 1. Lưu 3 file vật lý mới và cập nhật thông tin đường dẫn vào bảng documents (Ghi đè lịch sử)
            saveDocument(frontPart, userId, "id_card_front", uploadPath);
            saveDocument(backPart, userId, "id_card_back", uploadPath);
            saveDocument(facePart, userId, "other", uploadPath);

            // 2. [CẬP NHẬT LUỒNG GHI ĐÈ]: Đẩy trạng thái trên cả SQL và Session quay về 'pending'
            // Hàm này sẽ tự động chạy lệnh UPDATE để sửa trạng thái từ 'rejected' thành 'pending'
            boolean isUpdated = userDAO.updateOrInsertEkycStatus(userId, "pending");
            
            if (isUpdated) {
                // Cập nhật lại Session ngay lập tức để trang Dashboard chặn hoặc hiển thị đúng banner Vàng "Chờ duyệt"
                session.setAttribute("verification_status", "pending");
            }

            // 3. Điều hướng về hệ thống Dashboard tương ứng kèm tham số thông báo thành công
            if ("borrower".equals(role)) {
                // Điều hướng qua Servlet trung gian của Borrower để làm mới lại dữ liệu hiển thị
                response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard&msg=ekyc_updated_success");
            } else if ("investor".equals(role)) {
                response.sendRedirect("investor_dashboard.jsp?msg=ekyc_updated_success");
            } else {
                response.sendRedirect("index.jsp");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ekyc.jsp?error=uploadFailed");
        }
    }

    private void saveDocument(Part part, Long userId, String type, String uploadPath) throws IOException {
        if (part != null && part.getSize() > 0) {
            String fileName = userId + "_" + type + "_" + System.currentTimeMillis() + ".jpg";
            String fullPath = uploadPath + File.separator + fileName;
            
            part.write(fullPath);
            
            String fileUrl = "uploads/" + fileName;
            userDAO.insertDocument(userId, type, fileUrl);
        }
    }

    private boolean isEmpty(Part part) {
        return part == null || part.getSize() == 0;
    }
}