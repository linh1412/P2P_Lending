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
        String role = (String) session.getAttribute("role"); 
        
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 1. Xác định thư mục lưu ảnh
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        try {
            // 2. Xử lý lưu 3 ảnh vào thư mục và Database
            saveDocument(request.getPart("frontImg"), userId, "id_card_front", uploadPath);
            saveDocument(request.getPart("backImg"), userId, "id_card_back", uploadPath);
            saveDocument(request.getPart("faceImg"), userId, "other", uploadPath);

            // 3. ĐIỀU HƯỚNG DỰA TRÊN VAI TRÒ (ROLE)
            if ("borrower".equals(role)) {
                // Đẩy về Dashboard người vay
                response.sendRedirect("borrower-dashboard.jsp?msg=ekycSubmitted");
            } else if ("investor".equals(role)) {
                // Đẩy về Dashboard nhà đầu tư
                response.sendRedirect("investor-dashboard.jsp?msg=ekycSubmitted");
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
            // Tạo tên file: userId_loai_timestamp.jpg
            String fileName = userId + "_" + type + "_" + System.currentTimeMillis() + ".jpg";
            String fullPath = uploadPath + File.separator + fileName;
            
            // Lưu file vật lý
            part.write(fullPath);
            
            // Lưu vào DB
            String fileUrl = "uploads/" + fileName;
            userDAO.insertDocument(userId, type, fileUrl);
        }
    }
}