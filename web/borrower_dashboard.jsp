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
        
        .sidebar-menu li.disabled-menu a { color: #475569; cursor: not-allowed; background: none !important; }
        .sidebar-menu li.disabled-menu a:hover { color: #475569; background: none; }

        .btn-logout { background-color: #ef4444; color: white; padding: 14px 20px; text-decoration: none; display: flex; align-items: center; font-weight: bold; font-size: 15px; border: none; width: 100%; box-sizing: border-box; cursor: pointer; transition: background 0.2s;}
        .btn-logout:hover { background-color: #dc2626; }
        
        .main-content { margin-left: 260px; flex-grow: 1; display: flex; flex-direction: column; min-height: 100vh; }
        .topbar { background: #ffffff; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #e2e8f0; }
        .topbar-title { font-size: 18px; font-weight: bold; color: #1e293b; }
        .user-info { display: flex; align-items: center; gap: 15px; }
        
        .badge { padding: 6px 14px; border-radius: 20px; font-size: 12px; font-weight: bold; text-transform: uppercase; }
        .badge-success { background-color: #dcfce7; color: #15803d; }
        .badge-warning { background-color: #fef9c3; color: #a16207; }
        .badge-danger { background-color: #fef2f2; color: #991b1b; display: inline-block; text-decoration: none; }
        
        .container { padding: 30px; }
        
        .alert-banner { padding: 15px 20px; border-radius: 8px; margin-bottom: 25px; font-size: 14px; line-height: 1.5; border-left: 5px solid; }
        .alert-banner-danger { background-color: #fef2f2; border-color: #ef4444; color: #991b1b; }
        .alert-banner-warning { background-color: #fffbeb; border-color: #f59e0b; color: #92400e; }
        .alert-banner-success { background-color: #dcfce7; border-color: #22c55e; color: #15803d; }

        .btn-action-ekyc { display: inline-block; background-color: #f59e0b; color: #0f172a; padding: 8px 16px; font-weight: bold; text-decoration: none; border-radius: 6px; margin-top: 10px; font-size: 13px; transition: background 0.2s; }
        .btn-action-ekyc:hover { background-color: #d97706; }

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

        .preview-box { margin-top: 8px; display: none; text-align: center; background: #f1f5f9; padding: 10px; border-radius: 6px; border: 1px dashed #cbd5e1; }
        .preview-box img { max-width: 100%; max-height: 150px; border-radius: 4px; object-fit: contain; }
    </style>
</head>
<body>

    <div class="sidebar">
        <div>
            <div class="sidebar-brand">🏛️ <span>P2P LENDING</span></div>
            <ul class="sidebar-menu">
                <li class="${empty currentAction || currentAction == 'dashboard' ? 'active' : ''}" id="menu-dashboard">
                    <a href="${pageContext.request.contextPath}/BorrowerDashboardServlet?action=dashboard">📊 Tổng Quan Main</a>
                </li >
                
                <c:choose>
                    <c:when test="${trangThaiEkyc == 'rejected'}">
                        <li class="disabled-menu" onclick="alert('Không thể đăng ký: Hồ sơ eKYC của bạn đã bị từ chối! Vui lòng thực hiện cập nhật lại hồ sơ.')">
                            <a href="javascript:void(0);">🔒 Đăng Ký Vay Mới</a>
                        </li>
                    </c:when>
                    <c:when test="${hasActiveLoan}">
                        <li class="disabled-menu" onclick="alert('Không thể đăng ký: Bạn đang có một đơn vay chưa tất toán hoặc đang chờ duyệt!')">
                            <a href="javascript:void(0);">🔒 Đăng Ký Vay Mới</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="${currentAction == 'create_loan' ? 'active' : ''}">
                            <a href="${pageContext.request.contextPath}/BorrowerDashboardServlet?action=create_loan">📝 Đăng Ký Vay Mới</a>
                        </li>
                    </c:otherwise>
                </c:choose>

                <li class="${currentAction == 'market_loans' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/BorrowerDashboardServlet?action=market_loans">🌐 Khoản Vay Trên Sàn</a>
                </li>
            </ul>
        </div>
        <form action="${pageContext.request.contextPath}/logout" method="POST" style="margin:0;">
            <button type="submit" class="btn-logout">🚪 Đăng Xuất</button>
        </form>
    </div>

    <div class="main-content">
        <div class="topbar">
            <div class="topbar-title" id="dynamic-topbar-title">
                <c:choose>
                    <c:when test="${currentAction == 'create_loan'}">Đăng Ký Khoản Vay Mới</c:when>
                    <c:when test="${currentAction == 'market_loans'}">Khoản Vay Đang Gọi Vốn Toàn Sàn</c:when>
                    <c:when test="${currentAction == 're_ekyc'}">Cập Nhật Thông Tin Định Danh eKYC</c:when>
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
                        <span class="badge badge-danger" title="Hồ sơ định danh bị lỗi" style="cursor:help;">⚠️ EKYC BỊ TỪ CHỐI</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-warning">CHỜ DUYỆT EKYC</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="container">
            
            <%-- THÔNG BÁO TRẠNG THÁI CẬP NHẬT EKYC THÀNH CÔNG --%>
            <c:if test="${param.msg == 'ekyc_updated_success'}">
                <div class="alert-banner alert-banner-success">
                    <strong>🎉 Thành công:</strong> Hồ sơ eKYC của bạn đã được gửi lại thành công. Trạng thái tài khoản hiện tại chuyển về <b>Chờ duyệt (Pending)</b>. Vui lòng đợi hệ thống kiểm tra.
                </div>
            </c:if>

            <%-- THÔNG BÁO THÀNH CÔNG KHI SUBMIT ĐƠN VAY MỚI --%>
            <c:if test="${param.msg == 'loan_submit_success'}">
                <div class="alert-banner alert-banner-success">
                    <strong>🎉 Đăng ký thành công:</strong> Đơn đăng ký khoản vay của bạn đã được tiếp nhận và chuyển sang trạng thái <b>Chờ duyệt</b>. Ban quản trị sẽ sớm thẩm định tệp hồ sơ CIC PDF đính kèm.
                </div>
            </c:if>

            <%-- ĐOẠN ĐÃ SỬA: PHỐI HỢP THÊM PARAMETER ĐỂ TRÁNH LỖI ĐIỀU HƯỚNG CỦA SERVLET --%>
            <c:if test="${param.msg == 'error_ekyc_rejected' || (trangThaiEkyc == 'rejected' && currentAction != 're_ekyc')}">
                <div class="alert-banner alert-banner-danger" id="rejected-warning-banner">
                    <strong>⚠️ Quyền truy cập bị hạn chế:</strong> Hồ sơ định danh cá nhân (eKYC) của bạn hiện đang ở trạng thái <b>Từ chối (Rejected)</b>. Hệ thống đã khóa chức năng đăng ký khoản vay. 
                    <br>
                    <a href="${pageContext.request.contextPath}/BorrowerDashboardServlet?action=re_ekyc" 
                       onclick="this.href=this.href + '&currentAction=re_ekyc';" 
                       class="btn-action-ekyc">🔄 Cập nhật lại thông tin eKYC ngay</a>
                </div>
            </c:if>
            
            <c:if test="${param.msg == 'error_already_has_loan' || (hasActiveLoan && (empty currentAction || currentAction == 'dashboard'))}">
                <div class="alert-banner alert-banner-warning">
                    <strong>ℹ️ Thông báo hạn mức:</strong> Bạn đang có một yêu cầu vay đang trong trạng thái xử lý hồ sơ (Chờ duyệt) hoặc một khoản nợ chưa tất toán hoàn toàn trên hệ thống. Để đảm bảo an toàn tài chính, bạn chỉ được phép tạo đơn mới sau khi hoàn tất nghĩa vụ của đơn vay cũ.
                </div>
            </c:if>

            <c:choose>
                <%-- TAB 1: TỔNG QUAN MAIN --%>
                <c:when test="${empty currentAction || currentAction == 'dashboard'}">
                    <div id="main-dashboard-view">
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
                                        <th style="text-align: center;">XEM CIC</th>
                                        <th>TRẠNG THÁI HỒ SƠ</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty myLoansList}">
                                            <c:forEach var="myLoan" items="${myLoansList}">
                                                <tr>
                                                    <td>#<strong><c:out value="${myLoan.applicationId}"/></strong></td>
                                                    <td><strong><fmt:formatNumber value="${myLoan.amountRequested}" type="number" groupingUsed="true"/> đ</strong></td>
                                                    <td><c:out value="${myLoan.termMonths}"/> Tháng</td>
                                                    <td><fmt:formatDate value="${myLoan.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                    <td style="text-align: center;">
                                                        <c:choose>
                                                            <c:when test="${not empty myLoan.cicPdfUrl}">
                                                                <a href="${myLoan.cicPdfUrl}" target="_blank" style="color:#2563eb; text-decoration:underline; font-weight: bold;">
                                                                    📄 Xem PDF
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="color: #94a3b8; font-style: italic; font-size: 13px;">Chưa có file</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${myLoan.status == 'pending'}"><span style="color:#a16207; font-weight:600;">Chờ duyệt</span></c:when>
                                                            <c:when test="${myLoan.status == 'approved'}"><span style="color:#2563eb; font-weight:600;">Đã duyệt</span></c:when>
                                                            <c:when test="${myLoan.status == 'funded'}"><span style="color:#16a34a; font-weight:600;">Đã gọi vốn</span></c:when>
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
                    </div>
                </c:when>

                <%-- TAB ĐẶC BIỆT: CẬP NHẬT LẠI EKYC BẰNG FILE ẢNH KHI BỊ REJECTED --%>
                <c:when test="${currentAction == 're_ekyc'}">
                    <div class="data-card" id="re-ekyc-form-view" style="max-width: 600px; margin: 0 auto;">
                        <h4>🔄 Làm mới hồ sơ định danh cá nhân (eKYC)</h4>
                        <p style="font-size: 13px; color: #64748b; margin-bottom: 20px;">Vui lòng chụp và tải lại ảnh giấy tờ rõ ràng, chính chủ để gửi lại Ban quản trị xét duyệt (Ghi đè SQL trạng thái cũ).</p>
                        
                        <form action="${pageContext.request.contextPath}/BorrowerDashboardServlet" method="POST" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="update_ekyc">
                            
                            <div class="form-group">
                                <label>Ảnh mặt trước CCCD / CMND <span style="color:red;">*</span></label>
                                <input type="file" name="cccd_front" class="form-control" accept="image/*" required onchange="previewImage(this, 'front_preview')">
                                <div id="front_preview" class="preview-box">
                                    <img src="" alt="Mặt trước CCCD Preview">
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Ảnh mặt sau CCCD / CMND <span style="color:red;">*</span></label>
                                <input type="file" name="cccd_back" class="form-control" accept="image/*" required onchange="previewImage(this, 'back_preview')">
                                <div id="back_preview" class="preview-box">
                                    <img src="" alt="Mặt sau CCCD Preview">
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Ảnh chân dung chụp cùng CCCD (Selfie) <span style="color:red;">*</span></label>
                                <input type="file" name="selfie_avatar" class="form-control" accept="image/*" required onchange="previewImage(this, 'selfie_preview')">
                                <div id="selfie_preview" class="preview-box">
                                    <img src="" alt="Ảnh chân dung Preview">
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Thu nhập hằng tháng kê khai (VNĐ)</label>
                                <input type="number" name="monthlyIncome" class="form-control" value="${not empty borrowerObj.monthlyIncome ? borrowerObj.monthlyIncome : ''}" required oninput="previewCurrencyUpdate(this.value)">
                                <div id="updateCurrencyPreview" class="currency-preview"></div>
                                <span class="form-hint">Hạn mức sẽ tự động tính toán lại sau khi tài khoản chuyển về trạng thái hợp lệ.</span>
                            </div>
                            
                            <button type="submit" class="btn-submit" style="background-color: #f59e0b; color: #0f172a;">🚀 Gửi lại hồ sơ kiểm duyệt</button>
                        </form>
                    </div>
                </c:when>

                <%-- TAB 2: ĐĂNG KÝ VAY --%>
                <c:when test="${currentAction == 'create_loan'}">
                    <c:choose>
                        <c:when test="${trangThaiEkyc == 'verified' && !hasActiveLoan}">
                            <div class="data-card" style="max-width: 600px; margin: 0 auto;">
                                <h4>📝 Tạo Đơn Đăng Ký Vay Mới</h4>
                                
                                <form action="${pageContext.request.contextPath}/BorrowerDashboardServlet" method="POST" id="loanForm" onsubmit="return validateForm()">
                                    <input type="hidden" name="action" value="submit_loan">
                                    
                                    <div class="form-group">
                                        <label for="soTienVay">Số tiền yêu cầu gọi vốn (VNĐ)</label>
                                        <input type="number" id="soTienVay" name="amountRequested" min="1000000" data-max="${not empty hanMucToiDa ? hanMucToiDa : 0}" class="form-control" required oninput="previewCurrency(this.value)">
                                        <div id="currencyPreview" class="currency-preview"></div>
                                        <span class="form-hint">
                                            Hạn mức tối đa được phép vay: <strong style="color: var(--primary-color);"><fmt:formatNumber value="${hanMucToiDa}" type="number"/> đ</strong>
                                        </span>
                                    </div>
                                    <div class="form-group">
                                        <label for="kyHan">Kỳ hạn (Tháng)</label>
                                        <input type="number" id="kyHan" name="termMonths" min="1" max="60" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="ngayCapCic">Ngày cấp CIC cá nhân</label>
                                        <input type="date" id="ngayCapCic" name="cicIssuedDate" class="form-control" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="urlFileCic">Đường dẫn file PDF CIC</label>
                                        <input type="url" id="urlFileCic" name="cicPdfUrl" placeholder="https://example.com/your-cic.pdf" class="form-control" required>
                                    </div>
                                    <button type="submit" class="btn-submit">Xác Nhận Gửi Đơn Vay</button>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-lock">
                                <span style="font-size: 40px;">🔒</span>
                                <h3>Chức năng đăng ký vay đã bị khóa</h3>
                                <p>Tài khoản của bạn hiện thuộc một trong các trường hợp:</p>
                                <ul style="text-align: left; max-width: 380px; margin: 10px auto; color: #475569; font-size: 14px;">
                                    <li>Chưa xác thực eKYC hoặc eKYC bị từ chối.</li>
                                    <li>Đang có một khoản vay khác đang hoạt động dở dang.</li>
                                </ul>
                                <p style="font-size: 14px; color: #64748b; margin-top: 15px;">Vui lòng quay lại màn hình Tổng Quan để kiểm tra chi tiết lỗi.</p>
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
                                    <th>KÝ HẠN</th>
                                    <th>NỘI DUNG</th>
                                    <th>TIẾN ĐỘ GỌI VỐN</th>
                                </tr>
                            </table>
                            <table class="table-loan">
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty marketLoansList}">
                                        <c:forEach var="loan" items="${marketLoansList}">
                                            <tr>
                                                <td><strong><c:out value="${loan.applicationId}"/></strong></td>
                                                <td><span style="color:#64748b;">User_<c:out value="${loan.borrowerId}"/></span></td>
                                                <td><span style="padding:4px 8px; border-radius:4px; font-size:12px; background:#fef9c3; color:#a16207; font-weight:bold;">Mức thấp</span></td>
                                                <td><strong><fmt:formatNumber value="${loan.amountRequested}" type="number" groupingUsed="true"/> đ</strong></td>
                                                <td><c:out value="${loan.termMonths}"/> Tháng</td>
                                                <td><i style="color:#94a3b8;">Gọi vốn tiêu dùng dùng CIC cá nhân</i></td>
                                                <td>
                                                    <c:set var="percent" value="${not empty loan.amountRequested && loan.amountRequested gt 0 ? (loan.amountRaised * 100 / loan.amountRequested) : 0}"/>
                                                    <div style="width: 80px; background: #e2e8f0; border-radius: 10px; height: 6px; display: inline-block; margin-right: 5px;">
                                                        <div style="width: ${percent}%; background: #22c55e; height: 100%; border-radius: 10px;"></div>
                                                    </div>
                                                    <span style="font-size:12px; font-weight:bold; color:#16a34a;"><fmt:formatNumber value="${percent}" maxFractionDigits="0"/>%</span>
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
        // TỰ ĐỘNG BẮT LỖI HOẶC ÉP CHUYỂN GIAO DIỆN CLIENT-SIDE NẾU SERVLET KHÔNG ĐỔI CURRENT_ACTION
        window.addEventListener('DOMContentLoaded', () => {
            const urlParams = new URLSearchParams(window.location.search);
            const actionParam = urlParams.get('action');
            
            if (actionParam === 're_ekyc') {
                // Đổi trạng thái active sidebar menu sang mục Đăng ký nếu cần thiết hoặc hủy active Tổng quan
                const activeMenu = document.querySelector('.sidebar-menu li.active');
                if (activeMenu) activeMenu.classList.remove('active');
                
                // Cập nhật tiêu đề Topbar sang giao diện eKYC
                const topbarTitle = document.getElementById('dynamic-topbar-title');
                if (topbarTitle) topbarTitle.innerText = "Cập Nhật Thông Tin Định Danh eKYC";
            }
        });

        // Hàm đọc file ảnh instant preview
        function previewImage(input, previewId) {
            const previewBox = document.getElementById(previewId);
            const imgTag = previewBox.querySelector('img');
            
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    imgTag.src = e.target.result;
                    previewBox.style.display = 'block';
                }
                reader.readAsDataURL(input.files[0]);
            } else {
                imgTag.src = "";
                previewBox.style.display = 'none';
            }
        }

        function previewCurrency(value) {
            const previewDiv = document.getElementById('currencyPreview');
            formatVND(value, previewDiv);
        }

        function previewCurrencyUpdate(value) {
            const previewDiv = document.getElementById('updateCurrencyPreview');
            formatVND(value, previewDiv);
        }

        function formatVND(value, element) {
            if (!value || isNaN(value)) {
                element.innerText = '';
                return;
            }
            let formatter = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            });
            element.innerText = '👉 Quy đổi định dạng: ' + formatter.format(value);
        }

        function validateForm() {
            const inputSotien = document.getElementById('soTienVay');
            const soTienVay = parseFloat(inputSotien.value);
            const hanMucToiDa = parseFloat(inputSotien.getAttribute('data-max')) || 0;
            const ngayCapCic = new Date(document.getElementById('ngayCapCic').value);
            const today = new Date();

            if (soTienVay > hanMucToiDa) {
                alert("Số tiền đăng ký vay vượt quá hạn mức cho phép của bạn (" + hanMucToiDa.toLocaleString('vi-VN') + " đ)!");
                return false;
            }
            
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