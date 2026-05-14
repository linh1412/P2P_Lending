<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%-- Khai báo thư viện JSTL để sử dụng thẻ c:if --%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập P2P</title>
</head>
<body>
    <div style="width: 350px; margin: 100px auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); font-family: sans-serif;">
        <h2 style="text-align: center; color: #333;">Đăng nhập P2P</h2>

        <!-- PHẦN HIỂN THỊ THÔNG BÁO -->
        <div style="margin-bottom: 15px;">
            <%-- 1. Thông báo lỗi khi sai Email/Mật khẩu hoặc tài khoản chưa tồn tại --%>
            <c:if test="${param.error == 'invalid'}">
                <div style="color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 10px; border-radius: 5px; font-size: 14px; text-align: center;">
                    ⚠️ Sai email hoặc mật khẩu. Vui lòng kiểm tra lại!
                </div>
            </c:if>

            <%-- 2. Thông báo thành công sau khi vừa đăng ký xong --%>
            <c:if test="${param.msg == 'success'}">
                <div style="color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; padding: 10px; border-radius: 5px; font-size: 14px; text-align: center;">
                    ✅ Đăng ký thành công! Mời bạn đăng nhập.
                </div>
            </c:if>
        </div>

        <form action="LoginController" method="post">
            Email:<br>
            <input type="email" name="email" required 
                   style="width: 100%; padding: 10px; box-sizing: border-box; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;"><br>
            
            Mật khẩu:<br>
            <input type="password" name="password" required 
                   style="width: 100%; padding: 10px; box-sizing: border-box; margin-bottom: 20px; border: 1px solid #ccc; border-radius: 4px;"><br>
            
            <button type="submit" 
                    style="width: 100%; background: #007bff; color: white; border: none; padding: 12px; cursor: pointer; border-radius: 5px; font-weight: bold; font-size: 16px;">
                Đăng nhập
            </button>
        </form>

        <p style="text-align: center; margin-top: 20px; font-size: 14px;">
            Chưa có tài khoản? <a href="register.jsp" style="color: #007bff; text-decoration: none;">Đăng ký ngay</a>
        </p>
    </div>
</body>
</html>