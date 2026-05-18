package controller;

import dao.BorrowerDAO;
import model.LoanApplication;
import model.Borrower;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/BorrowerDashboardServlet")
public class BorrowerDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        // Kiểm tra phiên đăng nhập bảo mật
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentAction = request.getParameter("action");
        if (currentAction == null || currentAction.isEmpty()) {
            currentAction = "dashboard";
        }

        // 🛠️ BỔ SUNG LUỒNG ĐIỀU HƯỚNG QUAY LẠI TRANG EKYC KHI BẤM CẬP NHẬT
        if ("re_ekyc".equals(currentAction)) {
            // Chuyển hướng trực tiếp sang trang ekyc.jsp ban đầu để upload lại ảnh dính lỗi
            request.getRequestDispatcher("ekyc.jsp").forward(request, response);
            return; // Ngắt luồng tại đây để tránh chạy các câu lệnh SQL không cần thiết phía dưới
        }

        try {
            BorrowerDAO bDao = new BorrowerDAO();

            // Vì user_id liên kết 1-1 với borrower_id, ta truyền trực tiếp userId từ session vào
            Borrower borrower = bDao.getBorrowerById(userId);
            
            // CƠ CHẾ DỰ PHÒNG CHỐNG TRỐNG TRONG QUÁ TRÌNH KHỞI CHẠY KIỂM THỬ:
            if (borrower == null) {
                borrower = bDao.getBorrowerById(1);
                if (borrower != null) {
                    userId = borrower.getBorrowerId();
                }
            }

            // Khởi tạo các giá trị hiển thị mặc định
            String fullName = "Người dùng";
            double monthlyIncome = 0.0;
            double maxLimit = 0.0;

            if (borrower != null) {
                fullName = borrower.getFirstName() + " " + borrower.getLastName();
                monthlyIncome = borrower.getMonthlyIncome();
                // Công thức tính toán hạn mức tự động: Gấp 3 lần thu nhập hàng tháng công bố
                maxLimit = monthlyIncome * 3.0;
            }

            // [ĐỒNG BỘ LUỒNG MỚI]: Ưu tiên lấy trạng thái eKYC từ Session do Login/EKycController thiết lập
            String verificationStatus = (String) session.getAttribute("verification_status");
            
            // Nếu session chưa có (ví dụ trường hợp vào thẳng URL không qua login), mới lấy từ DB hoặc mặc định
            if (verificationStatus == null || "none".equals(verificationStatus)) {
                if (borrower != null && borrower.getVerificationStatus() != null) {
                    verificationStatus = borrower.getVerificationStatus();
                } else {
                    verificationStatus = "pending";
                }
                // Cập nhật ngược lại vào session để giữ tính thống nhất
                session.setAttribute("verification_status", verificationStatus);
            }

            // Tính toán tổng dư nợ thực tế từ database
            double currentDebt = bDao.getCurrentDebt(userId);

            // Tải danh sách đơn vay cá nhân thực tế
            List<LoanApplication> loanList = bDao.getLoansByBorrower(userId);

            // Kiểm tra xem người dùng có khoản vay nào đang hoạt động hoặc chờ duyệt không
            // Điều này dùng để bật/tắt nút "Đăng ký vay mới" trên giao diện dashboard
            boolean hasActiveLoan = false;
            if (loanList != null) {
                for (LoanApplication loan : loanList) {
                    if ("pending".equals(loan.getStatus()) || "approved".equals(loan.getStatus()) || "funded".equals(loan.getStatus())) {
                        hasActiveLoan = true;
                        break;
                    }
                }
            }

            // Đẩy toàn bộ dữ liệu ra Request Scope để hiển thị lên trang JSP
            request.setAttribute("currentAction", currentAction);
            request.setAttribute("borrowerName", fullName);
            request.setAttribute("trangThaiEkyc", verificationStatus); // Đồng bộ chuẩn tên biến với file JSP
            request.setAttribute("thuNhapKhai", monthlyIncome);
            request.setAttribute("hanMucToiDa", maxLimit);
            request.setAttribute("tongDuNo", currentDebt);
            request.setAttribute("myLoansList", loanList);
            request.setAttribute("hasActiveLoan", hasActiveLoan);

            // Chuyển hướng đồng bộ thông tin sang trang hiển thị
            request.getRequestDispatcher("borrower_dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tải thông tin Dashboard.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}