package controller;

import dao.BorrowerDAO;
import dao.LoanDAO;
import model.LoanApplication;
import model.Borrower;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/BorrowerDashboardServlet")
public class BorrowerDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentAction = request.getParameter("action");
        if (currentAction == null || currentAction.isEmpty()) {
            currentAction = "dashboard";
        }

        try {
            BorrowerDAO bDao = new BorrowerDAO();
            LoanDAO lDao = new LoanDAO();
            
            Borrower borrower = bDao.getBorrowerById(userId);
            
            if (borrower == null) {
                borrower = bDao.getBorrowerById(1L);
                if (borrower != null) {
                    userId = borrower.getBorrowerId();
                }
            }

            String fullName = "Người dùng";
            double monthlyIncome = 0.0;
            double maxLimit = 0.0;

            if (borrower != null) {
                fullName = borrower.getFirstName() + " " + borrower.getLastName();
                monthlyIncome = borrower.getMonthlyIncome();
                maxLimit = monthlyIncome * 3.0; 
            }

            String verificationStatus = (String) session.getAttribute("verification_status");
            if (verificationStatus == null || "none".equals(verificationStatus)) {
                if (borrower != null && borrower.getVerificationStatus() != null) {
                    verificationStatus = borrower.getVerificationStatus();
                } else {
                    verificationStatus = "pending";
                }
                session.setAttribute("verification_status", verificationStatus);
            }

            // Gọi đồng bộ từ lDao
            List<LoanApplication> loanList = lDao.getLoansByBorrower(userId);

            boolean hasActiveLoan = false;
            if (loanList != null) {
                for (LoanApplication loan : loanList) {
                    if ("pending".equals(loan.getStatus()) || "approved".equals(loan.getStatus()) || "funded".equals(loan.getStatus())) {
                        hasActiveLoan = true;
                        break;
                    }
                }
            }

            if ("re_ekyc".equals(currentAction)) {
                request.setAttribute("borrowerObj", borrower);
            } 
            else if ("market_loans".equals(currentAction)) {
                List<LoanApplication> marketLoans = lDao.getAllMarketLoans(); 
                request.setAttribute("marketLoansList", marketLoans);
            }

            double currentDebt = bDao.getCurrentDebt(userId);

            request.setAttribute("currentAction", currentAction);
            request.setAttribute("borrowerName", fullName);
            request.setAttribute("trangThaiEkyc", verificationStatus);
            request.setAttribute("thuNhapKhai", monthlyIncome);
            request.setAttribute("hanMucToiDa", maxLimit);
            request.setAttribute("tongDuNo", currentDebt);
            request.setAttribute("myLoansList", loanList);
            request.setAttribute("hasActiveLoan", hasActiveLoan);

            request.getRequestDispatcher("borrower_dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Hệ thống gặp sự cố khi tải bảng điều khiển.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        BorrowerDAO bDao = new BorrowerDAO();
        LoanDAO lDao = new LoanDAO();

        try {
            if ("submit_loan".equals(action)) {
                String amountStr = request.getParameter("amountRequested");
                String termStr = request.getParameter("termMonths");
                String cicIssuedDateStr = request.getParameter("cicIssuedDate");
                String cicPdfUrl = request.getParameter("cicPdfUrl");

                double amountRequested = (amountStr != null && !amountStr.isEmpty()) ? Double.parseDouble(amountStr) : 0.0;
                int termMonths = (termStr != null && !termStr.isEmpty()) ? Integer.parseInt(termStr) : 0;

                List<LoanApplication> loanList = lDao.getLoansByBorrower(userId);
                boolean hasActiveLoan = false;
                if (loanList != null) {
                    for (LoanApplication loan : loanList) {
                        if ("pending".equals(loan.getStatus()) || "approved".equals(loan.getStatus()) || "funded".equals(loan.getStatus())) {
                            hasActiveLoan = true;
                            break;
                        }
                    }
                }

                if (hasActiveLoan) {
                    response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard&msg=error_already_has_loan");
                    return;
                }

                LoanApplication newLoan = new LoanApplication();
                newLoan.setBorrowerId(userId);
                newLoan.setAmountRequested(amountRequested);
                newLoan.setTermMonths(termMonths);
                newLoan.setCicPdfUrl(cicPdfUrl);
                
                if (cicIssuedDateStr != null && !cicIssuedDateStr.isEmpty()) {
                    try {
                        newLoan.setCicIssuedDate(Date.valueOf(cicIssuedDateStr));
                    } catch (IllegalArgumentException e) {
                        newLoan.setCicIssuedDate(new Date(System.currentTimeMillis()));
                    }
                } else {
                    newLoan.setCicIssuedDate(new Date(System.currentTimeMillis()));
                }
                
                newLoan.setStatus("pending");

                // Gọi hàm nạp chồng đối tượng vừa viết ở trên
                boolean success = lDao.insertLoanApplication(newLoan);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard&msg=loan_submit_success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=create_loan&msg=loan_submit_failed");
                }
                return;
            } 
            
            else if ("update_ekyc".equals(action)) {
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String incomeStr = request.getParameter("monthlyIncome");
                double monthlyIncome = (incomeStr != null && !incomeStr.isEmpty()) ? Double.parseDouble(incomeStr) : 0.0;

                Borrower updateBorrower = new Borrower();
                updateBorrower.setBorrowerId(userId);
                updateBorrower.setFirstName(firstName);
                updateBorrower.setLastName(lastName);
                updateBorrower.setMonthlyIncome(monthlyIncome);
                updateBorrower.setVerificationStatus("pending");

                // Gọi hàm nhận đối tượng vừa nâng cấp
                boolean success = bDao.updateBorrowerEkyc(updateBorrower);

                if (success) {
                    session.setAttribute("verification_status", "pending");
                    response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard&msg=ekyc_updated_success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=re_ekyc&msg=ekyc_updated_failed");
                }
                return;
            }
            
            response.sendRedirect(request.getContextPath() + "/BorrowerDashboardServlet?action=dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<div style='padding:20px; border:1px solid #ef4444; background:#fef2f2; color:#991b1b; font-family:sans-serif;'>");
            response.getWriter().println("<h2>💥 Đã xảy ra lỗi hệ thống xử lý Backend (Servlet):</h2>");
            response.getWriter().println("<pre style='background:#ffffff; padding:15px; border-radius:6px; border:1px solid #fca5a5; overflow-x:auto;'>");
            e.printStackTrace(new java.io.PrintWriter(response.getWriter()));
            response.getWriter().println("</pre>");
            response.getWriter().println("</div>");
        }
    }
}