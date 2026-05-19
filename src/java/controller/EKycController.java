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
import jakarta.servlet.http.Part; 

@WebServlet("/EKycController")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class EKycController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // CẬP NHẬT: Nếu người dùng bấm "Cập nhật lại eKYC" từ Dashboard khi bị từ chối (reject)
        if ("re_ekyc".equals(action)) {
            // Forward (chuyển tiếp nội bộ) sang trang ekyc.jsp để giữ nguyên ngữ cảnh hệ thống
            request.getRequestDispatcher("ekyc.jsp").forward(request, response);
            return;
        }
        
        // Mặc định nếu truy cập bậy bằng GET không có action rõ ràng thì đẩy về ekyc.jsp
        response.sendRedirect("ekyc.jsp");
    }

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
            uploadDir.mkdirs(); // Tạo toàn bộ cây thư mục nếu chưa có
        }

        try {
            // LƯU Ý: Đảm bảo thuộc tính name trong các thẻ <input type="file"> ở file ekyc.jsp 
            // phải trùng khớp hoàn toàn với 3 chuỗi: "frontImg", "backImg", "faceImg"
            Part frontPart = request.getPart("frontImg");
            Part backPart = request.getPart("backImg");
            Part facePart = request.getPart("faceImg");

            // Kiểm tra tính hợp lệ (Tránh gửi form trống hoặc thiếu ảnh)
            if (isEmpty(frontPart) || isEmpty(backPart) || isEmpty(facePart)) {
                response.sendRedirect("ekyc.jsp?error=missingFiles");
                return;
            }

            // 1. Lưu 3 file vật lý mới và cập nhật thông tin đường dẫn vào DB (Ghi đè hoặc Thêm mới)
            saveDocument(frontPart, userId, "id_card_front", uploadPath);
            saveDocument(backPart, userId, "id_card_back", uploadPath);
            saveDocument(facePart, userId, "other", uploadPath); // 'other' đại diện cho ảnh selfie chân dung

            // 2. Đẩy trạng thái trên cả SQL và Session quay về 'pending' để Admin duyệt lại
            boolean isUpdated = userDAO.updateOrInsertEkycStatus(userId, "pending");
            
            if (isUpdated) {
                // Cập nhật lại Session ngay lập tức để hệ thống hiển thị đúng banner "Chờ duyệt"
                session.setAttribute("verification_status", "pending");
            }

            // 3. Điều hướng về hệ thống Dashboard tương ứng để làm mới (Refresh) dữ liệu hiển thị
            if ("borrower".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard&msg=ekyc_updated_success");
            } else if ("investor".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/InvestorDashboardServlet?action=dashboard&msg=ekyc_updated_success");
            } else {
                response.sendRedirect("index.jsp");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ekyc.jsp?error=uploadFailed");
        }
    }

    /**
     * Hàm hỗ trợ lưu ảnh vật lý vào Server và ghi log URL vào Database
     */
    private void saveDocument(Part part, Long userId, String type, String uploadPath) throws IOException {
        if (part != null && part.getSize() > 0) {
            // Lấy phần mở rộng thực tế của file (png, jpg, jpeg) thay vì ép cứng .jpg
            String extension = "jpg";
            String submittedFileName = part.getSubmittedFileName();
            if (submittedFileName != null && submittedFileName.contains(".")) {
                extension = submittedFileName.substring(submittedFileName.lastIndexOf(".") + 1);
            }

            // Định dạng tên file: userId_loaiAnh_Timestamp để tránh trùng lặp và không bị cache trình duyệt
            String fileName = userId + "_" + type + "_" + System.currentTimeMillis() + "." + extension;
            String fullPath = uploadPath + File.separator + fileName;
            
            // Ghi file vật lý vào thư mục /uploads trên server
            part.write(fullPath);
            
            // Lưu URL tương đối vào bảng documents
            String fileUrl = "uploads/" + fileName;
            
            // Gọi hàm xử lý ghi đè logic ở tầng DAO để không tạo ra bản ghi rác
            userDAO.saveOrUpdateDocument(userId, type, fileUrl);
        }
    }

    /**
     * Kiểm tra nhanh xem file up lên từ form có bị rỗng hay không
     */
    private boolean isEmpty(Part part) {
        return part == null || part.getSize() == 0 || part.getSubmittedFileName() == null || part.getSubmittedFileName().isEmpty();
    }
}