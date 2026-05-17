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

        // Cấu hình thư mục lưu ảnh
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

            // Lưu 3 file vật lý và lưu thông tin vào bảng documents
            saveDocument(frontPart, userId, "id_card_front", uploadPath);
            saveDocument(backPart, userId, "id_card_back", uploadPath);
            saveDocument(facePart, userId, "other", uploadPath);

            // TỰ ĐỘNG CHUYỂN HƯỚNG SANG TRANG DASHBOARD TƯƠNG ỨNG SAU KHI GỬI EKYC XONG
            if ("borrower".equals(role)) {
                // Nếu là Borrower -> Sang trang quản lý người vay
                response.sendRedirect("borrower_dashboard.jsp?msg=ekycSubmitted");
            } else if ("investor".equals(role)) {
                // Nếu là Investor -> Sang trang quản lý nhà đầu tư
                response.sendRedirect("investor_dashboard.jsp?msg=ekycSubmitted");
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