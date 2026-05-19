<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập Admin - P2P Lending</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body { 
            font-family: Arial, sans-serif; 
            background-color: #f8f9fa; 
            margin: 0; 
            padding: 0; 
            display: flex; 
            justify-content: center; 
            align-items: center; 
            height: 100vh; 
        }
        .login-box { 
            width: 400px; 
            background: white; 
            padding: 40px; 
            border-radius: 12px; 
            box-shadow: 0px 4px 20px rgba(0,0,0,0.1); 
        }
        h2 { text-align: center; color: #343a40; margin-bottom: 25px; font-size: 24px; }
        .form-group { margin-bottom: 20px; text-align: left; }
        .form-group label { display: block; font-weight: bold; margin-bottom: 5px; color: #555; font-size: 14px; }
        .form-group input { 
            width: 100%; 
            padding: 12px; 
            border: 1px solid #ccc; 
            border-radius: 6px; 
            box-sizing: border-box; 
            font-size: 15px;
        }
        .form-group input:focus { border-color: #343a40; outline: none; }
        .error-msg { color: #dc3545; background-color: #f8d7da; padding: 10px; border-radius: 6px; text-align: center; margin-bottom: 15px; font-size: 14px; }
        .btn-submit { 
            width: 100%; 
            padding: 14px; 
            background-color: #343a40; 
            color: white; 
            border: none; 
            border-radius: 6px; 
            font-size: 16px; 
            font-weight: bold; 
            cursor: pointer; 
            transition: background 0.2s; 
        }
        .btn-submit:hover { background-color: #23272b; }
        .back-home { display: block; text-align: center; margin-top: 20px; color: #007bff; text-decoration: none; font-size: 14px; }
        .back-home:hover { text-decoration: underline; }
    </style>
</head>
<body>

    <div class="login-box">
        <h2><i class="fa-solid fa-user-lock"></i> ĐĂNG NHẬP ADMIN</h2>
        
        <%-- Hiển thị thông báo lỗi nếu tài khoản hoặc mật khẩu sai --%>
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-msg">
                <i class="fa-solid fa-circle-exclamation"></i> <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <form action="AdminLoginServlet" method="POST">
            <div class="form-group">
                <label><i class="fa-solid fa-envelope"></i> Email hệ thống:</label>
                <input type="email" name="email" placeholder="Nhập email admin..." required>
            </div>
            
            <div class="form-group">
                <label><i class="fa-solid fa-key"></i> Mật khẩu bảo mật:</label>
                <input type="password" name="password" placeholder="Nhập mật khẩu..." required>
            </div>
            
            <button type="submit" class="btn-submit">Vào Hệ Thống Admin</button>
        </form>
        
        <a href="index.jsp" class="back-home"><i class="fa-solid fa-arrow-left"></i> Quay lại trang chủ</a>
    </div>

</body>
</html>