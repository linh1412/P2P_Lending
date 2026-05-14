package controller;

import dao.LoanDAO;
import model.LoanApplication;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/LoanController")
@MultipartConfig
public class LoanController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        try {
            // 1. Lấy dữ liệu từ create_loan.jsp
            double amount = Double.parseDouble(request.getParameter("amount_requested"));
            int term = Integer.parseInt(request.getParameter("term_months"));
            Date cicDate = Date.valueOf(request.getParameter("cic_issued_date"));

            // 2. Xử lý upload file CIC (Đúng yêu cầu File Management)
            Part filePart = request.getPart("cic_file");
            String fileName = "CIC_App_" + userId + "_" + System.currentTimeMillis() + ".pdf";
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();
            filePart.write(uploadPath + File.separator + fileName);
            
            String cicUrl = "uploads/" + fileName;

            // 3. Lưu vào Database
            LoanApplication loan = new LoanApplication(userId, amount, term, cicDate, cicUrl);
            LoanDAO dao = new LoanDAO();

            if (dao.createLoan(loan)) {
                response.sendRedirect("borrower_dashboard.jsp?msg=success");
            } else {
                response.sendRedirect("create_loan.jsp?error=db_error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("create_loan.jsp?error=invalid_data");
        }
    }
}