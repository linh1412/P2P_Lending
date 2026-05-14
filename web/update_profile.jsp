<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cập nhật hồ sơ Người vay</title>
    <style>
        body { background: #f0f2f5; font-family: 'Segoe UI', Arial, sans-serif; }
        .container { width: 480px; margin: 50px auto; padding: 30px; background: white; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 8px; font-weight: bold; color: #444; }
        input[type="text"], input[type="number"], input[type="file"] { 
            width: 100%; padding: 12px; box-sizing: border-box; border: 1px solid #ddd; border-radius: 6px; font-size: 15px;
        }
        input:focus { border-color: #28a745; outline: none; box-shadow: 0 0 5px rgba(40,167,69,0.2); }
        button { 
            width: 100%; padding: 13px; background-color: #28a745; color: white; border: none; 
            border-radius: 6px; font-weight: bold; font-size: 16px; cursor: pointer; transition: 0.3s; 
        }
        button:hover { background-color: #218838; }
        .error-box { 
            background: #f8d7da; color: #721c24; padding: 12px; border-radius: 6px; 
            margin-bottom: 20px; border: 1px solid #f5c6cb; font-size: 14px; text-align: center;
        }
        .info-text { color: #6c757d; font-size: 12px; margin-top: 5px; display: block; }
    </style>
</head>
<body>

<div class="container">
    <h2 style="text-align: center; color: #333;">Thông tin hồ sơ KYC</h2>

    <%-- Xử lý hiển thị thông báo lỗi dựa trên tham số error từ Controller --%>
    <c:if test="${not empty param.error}">
        <div class="error-box">
            <c:choose>
                <c:when test="${param.error == 'duplicate_id'}">
                    <b>Lỗi:</b> Số CCCD này đã được sử dụng bởi tài khoản khác!
                </c:when>
                <c:when test="${param.error == 'invalid_id'}">
                    <b>Lỗi:</b> Số CCCD không hợp lệ. Vui lòng nhập đúng 12 chữ số!
                </c:when>
                <c:when test="${param.error == 'update_failed'}">
                    <b>Lỗi:</b> Không thể cập nhật hồ sơ. Vui lòng thử lại sau!
                </c:when>
                <c:otherwise>
                    <b>Lỗi:</b> Đã có lỗi xảy ra. Vui lòng kiểm tra lại thông tin!
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <form action="KYCController" method="post" enctype="multipart/form-data">
        
        <div class="form-group">
            <label>Họ (First Name):</label>
            <input type="text" name="first_name" required 
                   placeholder="Ví dụ: Hà" value="${borrower.firstName}">
        </div>

        <div class="form-group">
            <label>Tên (Last Name):</label>
            <input type="text" name="last_name" required 
                   placeholder="Ví dụ: Phương Linh" value="${borrower.lastName}">
        </div>

        <div class="form-group">
            <label>Số CCCD (12 chữ số):</label>
            <%-- pattern="\d{12}" đảm bảo chỉ cho phép nhập đúng 12 con số --%>
            <input type="text" name="id_card_number" required 
                   placeholder="Nhập 12 số CCCD" 
                   pattern="\d{12}" 
                   maxlength="12"
                   title="Số CCCD phải bao gồm đúng 12 chữ số"
                   value="${borrower.idCardNumber}">
            <span class="info-text">Định dạng: 12 chữ số (ví dụ: 001099123456)</span>
        </div>

        <div class="form-group">
            <label>Thu nhập hàng tháng (VNĐ):</label>
            <input type="number" name="monthly_income" step="0.01" required 
                   placeholder="Ví dụ: 12000000" value="${borrower.monthlyIncome}">
        </div>

        <div class="form-group">
            <label>Tải lên hồ sơ (Ảnh CCCD hoặc file CIC):</label>
            <input type="file" name="id_image" required accept="image/*,.pdf">
            <span class="info-text">Chấp nhận định dạng ảnh hoặc PDF. Lưu vào thư mục 'uploads'.</span>
        </div>

        <button type="submit">Cập nhật thông tin</button>
        
        <div style="text-align: center; margin-top: 15px;">
            <a href="BorrowerDashboardServlet" style="color: #6c757d; text-decoration: none; font-size: 14px;">Quay lại Bảng điều khiển</a>
        </div>
    </form>
</div>

</body>
</html>