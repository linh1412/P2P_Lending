<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Xác minh eKYC</title>
    <style>
        .ekyc-container { width: 500px; margin: 50px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background: #fff; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; font-weight: bold; margin-bottom: 5px; }
        .btn-upload { background: #28a745; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; width: 100%; }
    </style>
</head>
<body>
    <div class="ekyc-container">
        <h2>Xác minh danh tính (eKYC)</h2>
        <p>Vui lòng tải lên ảnh CCCD và ảnh chân dung để được phê duyệt quyền vay/đầu tư.</p>
        
        <form action="EKycController" method="POST" enctype="multipart/form-data">
            <div class="form-group">
                <label>Mặt trước CCCD:</label>
                <input type="file" name="frontImg" accept="image/*" required>
            </div>
            
            <div class="form-group">
                <label>Mặt sau CCCD:</label>
                <input type="file" name="backImg" accept="image/*" required>
            </div>
            
            <div class="form-group">
                <label>Ảnh chân dung (Selfie):</label>
                <input type="file" name="faceImg" accept="image/*" required>
            </div>
            
            <button type="submit" class="btn-upload">Gửi yêu cầu xác thực</button>
        </form>
    </div>
</body>
</html>