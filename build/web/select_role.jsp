
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Chọn vai trò</title>
    <style>
        .role-container {
            display: flex;
            gap: 20px;
            justify-content: center;
            margin-top: 50px;
        }
        .role-card {
            width: 250px;
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .role-card h3 { color: #28a745; }
        .role-card a {
            display: inline-block;
            margin-top: 15px;
            padding: 10px 20px;
            background: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <h2 style="text-align: center;">Chào mừng bạn! Hãy chọn vai trò để tiếp tục:</h2>
    
    <div class="role-container">
        <div class="role-card">
            <h3>Người vay (Borrower)</h3>
            <p>Bạn muốn đăng đơn vay vốn cho dự án cá nhân hoặc kinh doanh.</p>
            <a href="RoleController?role=borrower">Bắt đầu vay</a>
        </div>
        
        <div class="role-card">
            <h3>Nhà đầu tư (Investor)</h3>
            <p>Bạn muốn dùng vốn để sinh lời từ các dự án cho vay.</p>
            <a href="RoleController?role=investor">Bắt đầu đầu tư</a>
        </div>
    </div>
</body>
</html>