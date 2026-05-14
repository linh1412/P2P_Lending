<%@page import="model.User"%>
<%@page import="model.Borrower"%>
<%@page import="model.LoanApplication"%>
<%@page import="dao.BorrowerDAO"%>
<%@page import="dao.UserDAO"%>
<%@page import="dao.LoanDAO"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt" %> 

<%
    User user = (User) session.getAttribute("user");
    Long userId = (Long) session.getAttribute("userId");
    
    // Tự động hồi phục session user nếu bị mất
    if (user == null && userId != null) {
        user = new dao.UserDAO().getUserById(userId);
        session.setAttribute("user", user);
    }
    
    if (user != null) {
        BorrowerDAO bDao = new BorrowerDAO();
        try {
            Borrower b = bDao.getBorrowerById(user.getUser_id());
            if (b != null) {
                request.setAttribute("borrower", b);
                
                // NẠP DANH SÁCH ĐƠN VAY TỪ DATABASE
                LoanDAO lDao = new LoanDAO();
                List<LoanApplication> loanList = lDao.getLoansByBorrower(b.getBorrowerId());
                request.setAttribute("loanList", loanList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard Người Vay</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; padding: 20px; background: #f4f7f6; }
        .card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .status-badge { padding: 5px 12px; border-radius: 15px; font-size: 0.85em; font-weight: bold; text-transform: capitalize; }
        /* Màu sắc trạng thái đơn vay */
        .status-pending { background: #ffeaa7; color: #d35400; }
        .status-verified { background: #55efc4; color: #00b894; }
        .status-approved { background: #81ecec; color: #0097e6; }
        .status-rejected { background: #ff7675; color: white; }
        
        #success-alert { 
            background: #d4edda; color: #155724; padding: 15px; 
            border-radius: 8px; margin-bottom: 20px; border: 1px solid #c3e6cb;
            transition: opacity 1s ease-out;
        }

        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { text-align: left; padding: 12px; border-bottom: 1px solid #eee; }
        th { background: #f8f9fa; color: #636e72; font-weight: 600; }
        
        .btn { padding: 10px 20px; border-radius: 5px; text-decoration: none; color: white; display: inline-block; font-weight: 500; border: none; cursor: pointer; }
        .btn-primary { background: #0984e3; }
        .kyc-img { width: 120px; height: 120px; object-fit: cover; border-radius: 10px; border: 2px solid #eee; }
    </style>
</head>
<body>

    <c:if test="${param.msg == 'success'}">
        <div id="success-alert">
            🎉 <strong>Thành công!</strong> Đơn vay vốn của bạn đã được gửi hệ thống.
        </div>
        <script>
            setTimeout(function() {
                var alert = document.getElementById('success-alert');
                if (alert) {
                    alert.style.opacity = '0';
                    setTimeout(function() { alert.style.display = 'none'; }, 1000);
                }
            }, 3000);
        </script>
    </c:if>

    <h2>Chào mừng, ${borrower.firstName} ${borrower.lastName}</h2>

    <div class="card">
        <h3>Thông tin cá nhân</h3>
        <div style="display: flex; gap: 40px; align-items: center;">
            <div style="flex: 1;">
                <p><strong>Họ và tên:</strong> ${borrower.firstName} ${borrower.lastName}</p>
                <p><strong>Số CCCD:</strong> ${borrower.idCardNumber}</p>
                <p><strong>Thu nhập:</strong> <b style="color: #d63031;"><fmt:formatNumber value="${borrower.monthlyIncome}" type="number"/> VNĐ</b></p>
                <p><strong>Trạng thái hồ sơ:</strong> 
                    <span class="status-badge status-${borrower.verificationStatus}">
                        ${not empty borrower.verificationStatus ? borrower.verificationStatus : 'Chưa cập nhật'}
                    </span>
                </p>
            </div>
            <div style="text-align: center;">
                <img src="https://ui-avatars.com/api/?name=${borrower.firstName}+${borrower.lastName}&background=0D8ABC&color=fff&size=128" class="kyc-img">
            </div>
        </div>
        <div style="margin-top: 15px;">
            <a href="update_profile.jsp" class="btn btn-primary">Cập nhật hồ sơ</a>
        </div>
    </div>

    <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <h3>Lịch sử yêu cầu vay vốn</h3>
            <c:if test="${borrower.verificationStatus == 'verified'}">
                <a href="create_loan.jsp" class="btn btn-primary">Tạo đơn vay mới</a>
            </c:if>
        </div>

        <table>
            <thead>
                <tr>
                    <th>Mã đơn</th>
                    <th>Số tiền yêu cầu</th>
                    <th>Thời hạn</th>
                    <th>Ngày tạo</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty loanList}">
                        <c:forEach var="loan" items="${loanList}">
                            <tr>
                                <td>#${loan.applicationId}</td>
                                <td><fmt:formatNumber value="${loan.amountRequested}" type="number"/> VNĐ</td>
                                <td>${loan.termMonths} tháng</td>
                                <td><fmt:formatDate value="${loan.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                    <span class="status-badge status-${loan.status.toLowerCase()}">${loan.status}</span>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="5" style="text-align: center; color: #b2bec3; padding: 30px;">
                                Bạn chưa có đơn vay nào. 
                                <c:if test="${borrower.verificationStatus != 'verified'}">
                                    <i>(Vui lòng xác thực hồ sơ để tạo đơn)</i>
                                </c:if>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

</body>
</html>