<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng Nhập Hệ Thống - P2P Lending</title>
    <!-- Nhúng thư viện icon Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body { font-family: Arial, sans-serif; background-color: #f8f9fa; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-card { width: 400px; background: white; padding: 40px; border-radius: 12px; box-shadow: 0px 4px 20px rgba(0,0,0,0.1); }
        h2 { text-align: center; color: #333; margin-bottom: 25px; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; color: #555; font-weight: bold; font-size: 14px; }
        .form-group input { width: 100%; padding: 12px; border: 1px solid #ccc; border-radius: 6px; box-sizing: border-box; font-size: 15px; }
        .form-group input:focus { border-color: #28a745; outline: none; }
        
        .btn-submit { width: 100%; padding: 14px; background-color: #28a745; border: none; border-radius: 6px; color: white; font-size: 16px; font-weight: bold; cursor: pointer; transition: background 0.2s; }
        .btn-submit:hover { background-color: #218838; }
        
        /* Định dạng các thông báo Alert */
        .alert { padding: 12px; border-radius: 6px; font-size: 14px; margin-bottom: 20px; text-align: center; font-weight: bold; }
        .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        
        .footer-links { text-align: center; margin-top: 20px; font-size: 14px; }
        .footer-links a { color: #007bff; text-decoration: none; }
        .footer-links a:hover { text-decoration: underline; }
    </style>
</head>
<body>

    <div class="login-card">
        <h2><i class="fa-solid fa-lock" style="color: #28a745;"></i> ĐĂNG NHẬP</h2>

        <%
            // Kiểm tra xem có thông báo lỗi từ Controller gửi về không
            String error = request.getParameter("error");
            if ("invalid".equals(error)) {
        %>
            <div class="alert alert-danger">
                <i class="fa-solid fa-triangle-exclamation"></i> Sai Email hoặc Mật khẩu!
            </div>
        <% 
            } 
            
            // Kiểm tra xem có thông báo vừa đăng ký thành công nhảy sang không
            String msg = request.getParameter("msg");
            if ("success".equals(msg)) {
        %>
            <div class="alert alert-success">
                <i class="fa-solid fa-circle-check"></i> Đăng ký thành công! Mời bạn đăng nhập.
            </div>
        <% 
            } 
        %>

        <!-- Form gửi dữ liệu sang LoginServlet (Chính là đường dẫn của LoginController) -->
        <form action="LoginServlet" method="POST">
            <div class="form-group">
                <label><i class="fa-solid fa-envelope"></i> Địa chỉ Email</label>
                <input type="email" name="email" placeholder="Nhập email của bạn..." required>
            </div>
            
            <div class="form-group">
                <label><i class="fa-solid fa-key"></i> Mật khẩu</label>
                <input type="password" name="password" placeholder="Nhập mật khẩu..." required>
            </div>
            
            <button type="submit" class="btn-submit">Vào Hệ Thống</button>
        </form>

        <div class="footer-links">
            <a href="index.jsp"><i class="fa-solid fa-house"></i> Quay lại trang chủ</a> 
            | 
            <a href="register.jsp">Đăng ký ngay</a>
        </div>
    </div>

</body>
</html>