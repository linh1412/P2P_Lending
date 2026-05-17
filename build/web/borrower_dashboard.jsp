<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sàn P2P Lending - Bảng Điều Khiển</title>
    <style>
        :root { 
            --sidebar-bg: #0f172a; 
            --sidebar-active: #1e293b; 
            --bg-main: #f8fafc; 
            --primary-color: #3b82f6; 
            --primary-hover: #2563eb;
        }
        body { font-family: 'Segoe UI', system-ui, sans-serif; margin: 0; padding: 0; display: flex; background-color: var(--bg-main); min-height: 100vh; }
        
        .sidebar { width: 260px; background-color: var(--sidebar-bg); color: #ffffff; display: flex; flex-direction: column; justify-content: space-between; position: fixed; top: 0; bottom: 0; left: 0; z-index: 100; }
        .sidebar-brand { padding: 24px 20px; font-size: 20px; font-weight: bold; display: flex; align-items: center; border-bottom: 1px solid #1e293b; }
        .sidebar-brand span { margin-left: 10px; }
        .sidebar-menu { list-style: none; padding: 0; margin: 20px 0; flex-grow: 1; }
        .sidebar-menu li a { display: flex; align-items: center; padding: 14px 20px; color: #94a3b8; text-decoration: none; font-size: 15px; transition: all 0.2s; }
        .sidebar-menu li a:hover, .sidebar-menu li.active a { color: #ffffff; background-color: var(--sidebar-active); font-weight: 500; }
        .btn-logout { background-color: #ef4444; color: white; padding: 14px 20px; text-decoration: none; display: flex; align-items: center; font-weight: bold; font-size: 15px; border: none; width: 100%; box-sizing: border-box; cursor: pointer; transition: background 0.2s;}
        .btn-logout:hover { background-color: #dc2626; }
        
        .main-content { margin-left: 260px; flex-grow: 1; display: flex; flex-direction: column; min-height: 100vh; }
        .topbar { background: #ffffff; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #e2e8f0; }
        .topbar-title { font-size: 18px; font-weight: bold; color: #1e293b; }
        .user-info { display: flex; align-items: center; gap: 15px; }
        .badge { padding: 6px 14px; border-radius: 20px; font-size: 12px; font-weight: bold; text-transform: uppercase; }
        .badge-success { background-color: #dcfce7; color: #15803d; }
        .badge-warning { background-color: #fef9c3; color: #a16207; }
        .badge-danger { background-color: #fef2f2; color: #991b1b; }
        
        .container { padding: 30px; }
        .stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 30px; }
        .stat-card { background: #ffffff; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; }
        .stat-info h5 { margin: 0 0 8px 0; color: #64748b; font-size: 13px; }
        .stat-info h2 { margin: 0; font-size: 24px; color: #0f172a; }
        
        .data-card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; padding: 24px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        .data-card h4 { margin-top: 0; margin-bottom: 20px; color: #1e293b; }
        .table-loan { width: 100%; border-collapse: collapse; text-align: left; }
        .table-loan th { padding: 12px; border-bottom: 2px solid #e2e8f0; color: #64748b; font-size: 13px; text-transform: uppercase; }
        .table-loan td { padding: 14px 12px; border-bottom: 1px solid #edf2f7; color: #4a5568; font-size: 14px; }
        
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; font-weight: 600; color: #334155; margin-bottom: 8px; font-size: 14px; }
        .form-control { width: 100%; padding: 11px 14px; border: 1px solid #cbd5e1; border-radius: 6px; box-sizing: border-box; font-size: 14px; }
        .form-control:focus { outline: none; border-color: var(--primary-color); box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }
        .form-hint { color: #64748b; font-size: 13px; display: block; margin-top: 6px; }
        .currency-preview { margin-top: 5px; font-size: 13px; color: #10b981; font-weight: 500; }
        
        .btn-submit { width: 100%; padding: 12px; background-color: var(--primary-color); color: white; border: none; border-radius: 6px; font-size: 15px; font-weight: bold; cursor: pointer; transition: background 0.2s; }
        .btn-submit:hover { background-color: var(--primary-hover); }
        .alert-lock { background-color: #fef2f2; border: 1px solid #fca5a5; color: #991b1b; padding: 40px 30px; border-radius: 8px; text-align: center; max-width: 500px; margin: 40px auto; }
        .alert-lock h3 { margin: 15px 0 10px 0; }
    </style>
</head>
<body>

    <div class="sidebar">
        <div>
            <div class="sidebar-brand">🏛️ <span>P2P LENDING</span></div>
            <ul class="sidebar-menu">
                <li class="${currentAction == 'dashboard' || empty currentAction ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/borrower_dashboard?action=dashboard">📊 Tổng Quan Main</a>
                </li>
                <li class="${currentAction == 'create_loan' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/borrower_dashboard?action=create_loan">📝 Đăng Ký Vay Mới</a>
                </li>
                <li class="${currentAction == 'market_loans' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/borrower_dashboard?action=market_loans">🌐 Khoản Vay Trên Sàn</a>
                </li>
            </ul>
        </div>
        <form action="${pageContext.request.contextPath}/logout" method="POST" style="margin:0;">
            <button type="submit" class="btn-logout">🚪 Đăng Xuất</button>
        </form>
    </div>

    <div class="main-content">
        <div class="topbar">
            <div class="topbar-title">
                <c:choose>
                    <c:when test="${currentAction == 'create_loan'}">Đăng Ký Khoản Vay Mới</c:when>
                    <c:when test="${currentAction == 'market_loans'}">Khoản Vay Đang Gọi Vốn Toàn Sàn</c:when>
                    <c:otherwise>Bảng Điều Khiển Tổng Quan</c:otherwise>
                </c:choose>
            </div>
            <div class="user-info">
                <span>Xin chào, <strong><c:out value="${not empty borrowerName ? borrowerName : 'Người dùng'}"/></strong></span>
                
                <c:choose>
                    <c:when test="${trangThaiEkyc == 'verified'}">
                        <span class="badge badge-success">ĐÃ XÁC THỰC EKYC</span>
                    </c:when>
                    <c:when test="${trangThaiEkyc == 'rejected'}">
                        <span class="badge badge-danger">EKYC BỊ TỪ CHỐI</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-warning">CHỜ DUYỆT EKYC</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="container">
            <c:choose>
                <%-- TAB 1: TỔNG QUAN MAIN --%>
                <c:when test="${currentAction == 'dashboard' || empty currentAction}">
                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-info">
                                <h5>HẠN MỨC VAY TỐI ĐA</h5>
                                <h2><fmt:formatNumber value="${not empty hanMucToiDa ? hanMucToiDa : 0}" type="number" groupingUsed="true"/> đ</h2>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-info">
                                <h5>TỔNG DƯ NỢ HIỆN TẠI</h5>
                                <h2 style="color: #ef4444;"><fmt:formatNumber value="${not empty tongDuNo ? tongDuNo : 0}" type="number" groupingUsed="true"/> đ</h2>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-info">
                                <h5>THU NHẬP KÊ KHAI</h5>
                                <h2 style="color: #10b981;"><fmt:formatNumber value="${not empty thuNhapKhai ? thuNhapKhai : 0}" type="number" groupingUsed="true"/> đ</h2>
                            </div>
                        </div>
                    </div>

                    <div class="data-card">
                        <h4>📄 Hồ Sơ Giao Dịch Khoản Vay Của Bạn</h4>
                        <table class="table-loan">
                            <thead>
                                <tr>
                                    <th>MÃ ĐƠN VAY</th>
                                    <th>SỐ TIỀN ĐĂNG KÝ</th>
                                    <th>KỲ HẠN VAY</th>
                                    <th>NGÀY TẠO ĐƠN</th>
                                    <th>XEM CIC</th>
                                    <th>TRẠNG THÁI HỒ SƠ</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty myLoansList}">
                                        <c:forEach var="myLoan" items="${myLoansList}">
                                            <tr>
                                                <td><strong><c:out value="${myLoan[0]}"/></strong></td>
                                                <td><strong><fmt:formatNumber value="${myLoan[1]}" type="number" groupingUsed="true"/> đ</strong></td>
                                                <td><c:out value="${myLoan[2]}"/> Tháng</td>
                                                <td><fmt:formatDate value="${myLoan[3]}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                <td>
                                                    <c:if test="${not empty myLoan[4]}">
                                                        <a href="<c:out value='${myLoan[4]}'/>" target="_blank" style="color:var(--primary-color); text-decoration:none; font-weight: 500;">📄 Xem PDF</a>
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${myLoan[5] == 'pending'}"><span style="color:#a16207; font-weight:600;">Chờ duyệt</span></c:when>
                                                        <c:when test="${myLoan[5] == 'approved'}"><span style="color:#2563eb; font-weight:600;">Đã duyệt</span></c:when>
                                                        <c:otherwise><span style="color:#dc2626; font-weight:600;">Từ chối</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="6" style="text-align: center; color: #94a3b8; padding: 25px;">Bạn chưa có đơn đăng ký vay cá nhân nào.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </c:when>

                <%-- TAB 2: ĐĂNG KÝ VAY --%>
                <c:when test="${currentAction == 'create_loan'}">
                    <c:choose>
                        <c:when test="${trangThaiEkyc == 'verified'}">
                            <div class="data-card" style="max-width: 600px; margin: 0 auto;">
                                <h4>📝 Tạo Đơn Đăng Ký Vay Mới</h4>
                                <form action="${pageContext.request.contextPath}/borrower_dashboard" method="POST" id="loanForm" onsubmit="return validateForm()">
                                    <input type="hidden" name="action" value="submit_loan">
                                    
                                    <div class="form-group">
                                        <label for="soTienVay">Số tiền yêu cầu gọi vốn (VNĐ)</label>
                                        <input type="number" id="soTienVay" name="soTienVay" min="1000000" max="${hanMucToiDa}" class="form-control" required oninput="previewCurrency(this.value)">
                                        <div id="currencyPreview" class="currency-preview"></div>
                                        <span class="form-hint">
                                            Hạn mức tối đa được phép vay: <strong style="color: var(--primary-color);"><fmt:formatNumber value="${hanMucToiDa}" type="number"/> đ</strong>
                                        </span>
                                    </div>
                                    <div class="form-group">
                                        <label for="kyHan">Kỳ hạn (Tháng)</label>
                                        <input type="number" id="kyHan" name="kyHan" min="1" max="60" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="ngayCapCic">Ngày cấp CIC cá nhân</label>
                                        <input type="date" id="ngayCapCic" name="ngayCapCic" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="urlFileCic">Đường dẫn file PDF CIC</label>
                                        <input type="url" id="urlFileCic" name="urlFileCic" placeholder="https://example.com/your-cic.pdf" class="form-control" required>
                                    </div>
                                    <button type="submit" class="btn-submit">Xác Nhận Gửi Đơn Vay</button>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-lock">
                                <span style="font-size: 40px;">🔒</span>
                                <h3>Chức năng đăng ký vay đang bị khóa</h3>
                                <p>Tài khoản của bạn hiện chưa được xác thực eKYC thành công hoặc đang ở trạng thái chờ duyệt.</p>
                                <p style="font-size: 14px; color: #64748b; margin-top: 15px;">Vui lòng quay lại sau khi hồ sơ định danh được phê duyệt thành công thành `verified`.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:when>

                <%-- TAB 3: KHOẢN VAY TRÊN SÀN --%>
                <c:when test="${currentAction == 'market_loans'}">
                    <div class="data-card">
                        <h4>🌐 Chợ Gọi Vốn Toàn Sàn (Ẩn danh bảo mật)</h4>
                        <table class="table-loan">
                            <thead>
                                <tr>
                                    <th>MÃ ĐƠN VAY</th>
                                    <th>MÃ NGƯỜI VAY</th>
                                    <th>MỨC ĐỘ RỦI RO</th>
                                    <th>SỐ TIỀN VAY</th>
                                    <th>KỲ HẠN</th>
                                    <th>NỘI DUNG</th>
                                    <th>TIẾN ĐỘ GOI VỐN</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty marketLoansList}">
                                        <c:forEach var="loan" items="${marketLoansList}">
                                            <tr>
                                                <td><strong><c:out value="${loan[0]}"/></strong></td>
                                                <td><span style="color:#64748b;"><c:out value="${loan[1]}"/></span></td>
                                                <td><span style="padding:4px 8px; border-radius:4px; font-size:12px; background:#fef9c3; color:#a16207; font-weight:bold;"><c:out value="${loan[2]}"/></span></td>
                                                <td><strong><fmt:formatNumber value="${loan[3]}" type="number" groupingUsed="true"/> đ</strong></td>
                                                <td><c:out value="${loan[4]}"/> Tháng</td>
                                                <td><i style="color:#94a3b8;"><c:out value="${loan[5]}"/></i></td>
                                                <td>
                                                    <div style="width: 80px; background: #e2e8f0; border-radius: 10px; height: 6px; display: inline-block; margin-right: 5px;">
                                                        <div style="width: ${loan[6]}%; background: #22c55e; height: 100%; border-radius: 10px;"></div>
                                                    </div>
                                                    <span style="font-size:12px; font-weight:bold; color:#16a34a;"><c:out value="${loan[6]}"/>%</span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="7" style="text-align: center; color: #a0aec0; padding: 20px;">Không có đơn gọi vốn nào khác trên sàn.</td></tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </c:when>
            </c:choose>
        </div>
    </div>

    <script>
        // Hiển thị số tiền dạng có dấu phân cách nghìn khi người dùng nhập vào ô vay tiền
        function previewCurrency(value) {
            const previewDiv = document.getElementById('currencyPreview');
            if (!value || isNaN(value)) {
                previewDiv.innerText = '';
                return;
            }
            let formatter = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            });
            previewDiv.innerText = '👉 Bằng chữ/Định dạng: ' + formatter.format(value);
        }

        // Kiểm tra dữ liệu trước khi submit đơn đăng ký vay
        function validateForm() {
            const soTienVay = parseFloat(document.getElementById('soTienVay').value);
            const hanMucToiDa = parseFloat("${not empty hanMucToiDa ? hanMucToiDa : 0}");
            const ngayCapCic = new Date(document.getElementById('ngayCapCic').value);
            const today = new Date();

            if (soTienVay > hanMucToiDa) {
                alert("Số tiền đăng ký vay vượt quá hạn mức cho phép của bạn (" + hanMucToiDa.toLocaleString('vi-VN') + " đ)!");
                return false;
            }
            
            // Xóa giờ phút giây để so sánh ngày chuẩn xác
            today.setHours(0,0,0,0);
            if (ngayCapCic > today) {
                alert("Ngày cấp hồ sơ CIC không được là ngày trong tương lai!");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>