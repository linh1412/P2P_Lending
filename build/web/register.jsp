<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
        .form-container { width: 450px; margin: 30px auto; padding: 25px; background: white; border-radius: 8px; box-shadow: 0px 0px 15px #ccc; }
        .form-group { margin-bottom: 15px; position: relative; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group select { width: 100%; padding: 10px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        .password-wrapper { position: relative; }
        .toggle-password { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); cursor: pointer; color: #666; }
        .btn-submit { width: 100%; padding: 12px; background-color: #007bff; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; }
        .btn-submit:hover { background-color: #0056b3; }
        .error-text { color: red; font-size: 13px; margin-top: 5px; display: none; }
        .alert-error { color: red; background: #ffdada; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; font-size: 14px; }
        .login-link { text-align: center; margin-top: 20px; font-size: 15px; border-top: 1px solid #eee; padding-top: 15px; }
        .login-link a { color: #007bff; text-decoration: none; font-weight: bold; }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Đăng ký hệ thống P2P</h2>
        
        <%-- PHẦN BÁO LỖI CHI TIẾT --%>
        <% 
            String error = request.getParameter("error");
            if ("emailExists".equals(error)) { 
        %>
            <div class="alert-error">Email này đã được sử dụng. Vui lòng chọn email khác!</div>
        <% } else if ("invalidEmail".equals(error)) { %>
            <%-- ĐÃ THÊM: Hiển thị lỗi trả về từ Backend --%>
            <div class="alert-error">Đăng ký thất bại: Định dạng email không hợp lệ!</div>
        <% } else if ("dbError".equals(error)) { %>
            <div class="alert-error">Lỗi: CCCD đã tồn tại hoặc hệ thống gặp sự cố!</div>
        <% } %>
        
        <form action="RegisterController" method="POST" onsubmit="return validateForm()">
            <div class="form-group">
                <label>Bạn tham gia với vai trò:</label>
                <select name="role" id="role" onchange="toggleRoleFields()" required>
                    <option value="borrower">Người đi vay (Borrower)</option>
                    <option value="investor">Nhà đầu tư (Investor)</option>
                </select>
            </div>
            
            <div class="form-group">
                <label>Email:</label>
                <%-- Đust id và placeholder để người dùng dễ nhìn --%>
                <input type="email" name="email" id="email" placeholder="example@gmail.com" required>
                <%-- ĐÃ THÊM: Thẻ div hiển thị lỗi real-time cho ô email --%>
                <div id="email-error" class="error-text">Cấu trúc email không đúng (Ví dụ hợp lệ: abc@gmail.com).</div>
            </div>
            
            <div class="form-group">
                <label>Số điện thoại:</label>
                <input type="tel" name="phone" pattern="[0-9]{10}" placeholder="0912345678" required>
            </div>
            
            <div class="form-group">
                <label>Mật khẩu:</label>
                <div class="password-wrapper">
                    <input type="password" id="password" name="password" required>
                    <i class="fa-solid fa-eye toggle-password" onclick="togglePasswordVisibility('password', this)"></i>
                </div>
                <div id="password-error" class="error-text">Mật khẩu tối thiểu 8 ký tự, có chữ hoa và ký tự đặc biệt.</div>
            </div>
            
            <div class="form-group">
                <label>Xác nhận mật khẩu:</label>
                <div class="password-wrapper">
                    <input type="password" id="confirmPassword" required>
                    <i class="fa-solid fa-eye toggle-password" onclick="togglePasswordVisibility('confirmPassword', this)"></i>
                </div>
                <div id="confirm-error" class="error-text">Mật khẩu xác nhận không đúng!</div>
            </div>
            
            <div class="form-group">
                <label>Tên:</label>
                <input type="text" name="firstName" required>
            </div>
            
            <div class="form-group">
                <label>Họ:</label>
                <input type="text" name="lastName" required>
            </div>

            <div id="borrowerFields">
                <div class="form-group">
                    <label>Số CCCD (12 số):</label>
                    <input type="text" name="idCardNumber" id="idCardNumber">
                </div>
                <div class="form-group">
                    <label>Thu nhập (VND):</label>
                    <input type="number" name="monthlyIncome" id="monthlyIncome">
                </div>
            </div>

            <div id="investorFields" style="display: none;">
                <div class="form-group">
                    <label>Khẩu vị rủi ro:</label>
                    <select name="riskAppetite" id="riskAppetite">
                        <option value="Conservative">An toàn</option>
                        <option value="Moderate">Trung dung</option>
                        <option value="Aggressive">Mạo hiểm</option>
                    </select>
                </div>
            </div>

            <button type="submit" class="btn-submit">Hoàn tất đăng ký</button>
        </form>

        <%-- DÒNG CHUYỂN HƯỚNG SANG LOGIN --%>
        <div class="login-link">
            Bạn đã có tài khoản? <a href="login.jsp">Đăng nhập ngay</a>
        </div>
    </div>

    <script>
        function togglePasswordVisibility(inputId, icon) {
            const input = document.getElementById(inputId);
            input.type = input.type === "password" ? "text" : "password";
            icon.classList.toggle("fa-eye");
            icon.classList.toggle("fa-eye-slash");
        }

        function toggleRoleFields() {
            const role = document.getElementById("role").value;
            const isBorrower = role === "borrower";
            document.getElementById("borrowerFields").style.display = isBorrower ? "block" : "none";
            document.getElementById("investorFields").style.display = isBorrower ? "none" : "block";
            document.getElementById("idCardNumber").required = isBorrower;
            document.getElementById("monthlyIncome").required = isBorrower;
        }

        function validateForm() {
            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword").value;
            const role = document.getElementById("role").value;
            
            // Định nghĩa Regex kiểm tra cấu trúc email (yêu cầu nghiêm ngặt phần tên miền)
            const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            const passwordPattern = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,}$/;

            // Ẩn toàn bộ thông báo lỗi trước khi kiểm tra lại
            document.getElementById("email-error").style.display = "none";
            document.getElementById("password-error").style.display = "none";
            document.getElementById("confirm-error").style.display = "none";

            // 1. ĐÃ THÊM: Kiểm tra cấu trúc Email bằng Javascript
            if (!emailPattern.test(email)) {
                document.getElementById("email-error").style.display = "block";
                document.getElementById("email").focus();
                return false;
            }

            // 2. Kiểm tra mật khẩu
            if (!passwordPattern.test(password)) {
                document.getElementById("password-error").style.display = "block";
                document.getElementById("password").focus();
                return false;
            }
            if (password !== confirmPassword) {
                document.getElementById("confirm-error").style.display = "block";
                document.getElementById("confirmPassword").focus();
                return false;
            }
            if (role === "borrower") {
                const idCard = document.getElementById("idCardNumber").value;
                if (!/^[0-9]{12}$/.test(idCard)) {
                    alert("CCCD phải có đúng 12 chữ số!");
                    document.getElementById("idCardNumber").focus();
                    return false;
                }
            }
            return true;
        }
    </script>
</body>
</html>