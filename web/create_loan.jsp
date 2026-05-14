<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tạo đơn vay mới - P2P Lending</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f7f6; display: flex; justify-content: center; padding: 50px; }
        .form-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); width: 400px; }
        h2 { color: #333; text-align: center; margin-bottom: 20px; }
        label { display: block; margin-top: 15px; font-weight: bold; color: #555; }
        input { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        button { width: 100%; padding: 12px; background-color: #28a745; color: white; border: none; border-radius: 4px; margin-top: 25px; cursor: pointer; font-size: 16px; }
        button:hover { background-color: #218838; }
        .error { color: red; font-size: 14px; margin-top: 10px; text-align: center; }
        .success { color: green; font-size: 14px; margin-top: 10px; text-align: center; }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Đăng ký khoản vay</h2>
        
        <%-- Hiển thị thông báo lỗi nếu có --%>
        <c:if test="${param.error == 'upload_failed'}">
            <p class="error">Lỗi: Không thể tải lên file CIC. Vui lòng thử lại!</p>
        </c:if>

        <form action="LoanController" method="post" enctype="multipart/form-data">
            <label>Số tiền yêu cầu (VNĐ):</label>
            <input type="number" name="amount_requested" min="1000000" placeholder="Ví dụ: 10000000" required>

            <label>Kỳ hạn (tháng):</label>
            <input type="number" name="term_months" min="1" max="60" placeholder="Ví dụ: 12" required>

            <label>Ngày cấp báo cáo CIC:</label>
            <input type="date" name="cic_issued_date" required>

            <label>Tải báo cáo CIC (Chỉ nhận file PDF):</label>
            <input type="file" name="cic_file" accept="application/pdf" required>
            <small style="color: #666;">* File CIC là bắt buộc để xét duyệt đơn vay.</small>

            <button type="submit">Gửi đơn vay</button>
        </form>
        
        <div style="text-align: center; margin-top: 15px;">
            <a href="borrower_dashboard.jsp" style="color: #007bff; text-decoration: none; font-size: 14px;">Quay lại Dashboard</a>
        </div>
    </div>
</body>
</html>