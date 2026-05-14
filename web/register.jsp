<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %> 

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản P2P</title>
</head>
<body>
    <div style="width: 350px; margin: 50px auto; border: 1px solid #ddd; padding: 25px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); font-family: Arial, sans-serif;">
        <h2 style="text-align: center; color: #333; margin-bottom: 20px;">Đăng ký tài khoản</h2>

        <!-- VÙNG THÔNG BÁO -->
        <div style="margin-bottom: 15px;">
            <%-- Trường hợp 1: Email đã tồn tại (Màu vàng - Warning) --%>
            <c:if test="${param.error == 'exists'}">
                <div style="color: #856404; background-color: #fff3cd; border: 1px solid #ffeeba; padding: 12px; border-radius: 6px; font-size: 14px; text-align: center; line-height: 1.5;">
                    <strong>Thông báo:</strong> Email này đã được đăng ký trước đó.<br>
                    Bạn có muốn <a href="login.jsp" style="color: #0056b3; font-weight: bold; text-decoration: underline;">Đăng nhập ngay</a> không?
                </div>
            </c:if>

            <%-- Trường hợp 2: Mật khẩu không khớp (Màu đỏ - Error) --%>
            <c:if test="${param.error == 'mismatch'}">
                <div style="color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 12px; border-radius: 6px; font-size: 14px; text-align: center;">
                    ⚠️ Mật khẩu xác nhận không khớp. Vui lòng nhập lại!
                </div>
            </c:if>

            <%-- Trường hợp 3: Lỗi Server/Hệ thống (Màu đỏ đậm) --%>
            <c:if test="${param.error == 'server'}">
                <div style="color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 12px; border-radius: 6px; font-size: 14px; text-align: center;">
                    ❌ Hệ thống đang gặp sự cố. Vui lòng thử lại sau vài phút.
                </div>
            </c:if>
        </div>

        <!-- FORM ĐĂNG KÝ -->
        <form action="RegisterController" method="post">
            <label style="font-weight: bold; font-size: 14px;">Email:</label><br>
            <input type="email" name="email" required placeholder="example@gmail.com"
                   style="width: 100%; padding: 10px; box-sizing: border-box; margin-top: 5px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 5px;"><br>
            
            <label style="font-weight: bold; font-size: 14px;">Mật khẩu:</label><br>
            <input type="password" name="password" required 
                   style="width: 100%; padding: 10px; box-sizing: border-box; margin-top: 5px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 5px;"><br>
            
            <label style="font-weight: bold; font-size: 14px;">Xác nhận mật khẩu:</label><br>
            <input type="password" name="confirmPassword" required 
                   style="width: 100%; padding: 10px; box-sizing: border-box; margin-top: 5px; margin-bottom: 20px; border: 1px solid #ccc; border-radius: 5px;"><br>
            
            <button type="submit" 
                    style="width: 100%; background: #28a745; color: white; border: none; padding: 12px; cursor: pointer; border-radius: 6px; font-weight: bold; font-size: 16px;">
                Tạo tài khoản
            </button>
        </form>
        
        <p style="text-align: center; margin-top: 20px; font-size: 14px;">
            Đã có tài khoản? <a href="login.jsp" style="color: #1a73e8; text-decoration: none; font-weight: bold;">Đăng nhập</a>
        </p>
    </div>
</body>
</html>