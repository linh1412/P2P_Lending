package controller;

import dao.BorrowerDAO;
import model.Borrower;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;

@WebServlet("/KYCController")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class KYCController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String firstName = request.getParameter("first_name");
            String lastName = request.getParameter("last_name");
            String idCard = request.getParameter("id_card_number");
            String incomeStr = request.getParameter("monthly_income");
            double income = (incomeStr != null && !incomeStr.isEmpty()) ? Double.parseDouble(incomeStr) : 0;

            // --- BƯỚC MỚI: VALIDATE CCCD ---
            // 1. Kiểm tra đủ 12 số
            if (idCard == null || !idCard.matches("\\d{12}")) {
                response.sendRedirect("update_profile.jsp?error=invalid_id");
                return;
            }

            BorrowerDAO dao = new BorrowerDAO();
            
            // 2. Kiểm tra trùng lặp trong DB
            if (dao.isIdCardExists(idCard, userId)) {
                response.sendRedirect("update_profile.jsp?error=duplicate_id");
                return;
            }

            // --- Xử lý File ---
            Part filePart = request.getPart("id_image");
            String fileNameToSave = "";
            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = filePart.getSubmittedFileName();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String safeName = (firstName + "_" + lastName).replaceAll("\\s+", "");
                fileNameToSave = "User" + userId + "_" + safeName + fileExtension;

                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                
                filePart.write(uploadPath + File.separator + fileNameToSave);
            }

            // --- Lưu Database ---
            Borrower b = new Borrower(userId, firstName, lastName, idCard, income);
            if (dao.updateProfile(b)) {
                // QUAN TRỌNG: Điều hướng về Servlet hiển thị Dashboard, không điều hướng về .jsp trực tiếp
                // Giả sử Servlet của Linh là BorrowerDashboardServlet
                response.sendRedirect("BorrowerDashboardServlet?success=1"); 
            } else {
                response.sendRedirect("update_profile.jsp?error=update_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("update_profile.jsp?error=exception");
        }
    }
}