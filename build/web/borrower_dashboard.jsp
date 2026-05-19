<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sàn P2P Lending - Bảng Điều Khiển</title>
    <!-- Nhúng file CSS tách riêng của bạn ở đây -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <style>
        /* Các class CSS bổ sung riêng cho hiển thị 5 gói vay đẹp hơn */
        .package-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.02);
        }
        .package-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px dashed #e2e8f0;
            padding-bottom: 12px;
            margin-bottom: 15px;
        }
        .package-title {
            font-size: 16px;
            font-weight: bold;
            color: #0f172a;
        }
        .package-limit {
            font-size: 15px;
            font-weight: 700;
            color: #2563eb;
        }
        .package-desc {
            font-size: 14px;
            color: #475569;
            margin-bottom: 15px;
            line-height: 1.5;
        }
        .sub-config-title {
            font-size: 13px;
            font-weight: 600;
            color: #64748b;
            text-transform: uppercase;
            margin-bottom: 8px;
        }
        .sub-config-list {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }
        .sub-config-item {
            background: #f1f5f9;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            padding: 8px 12px;
            font-size: 13px;
            color: #1e293b;
        }
        .sub-config-item strong {
            color: #16a34a;
        }
    </style>
</head>
<body>

    <div class="sidebar">
        <div>
            <div class="sidebar-brand">🏛️ <span>P2P LENDING</span></div>
            <ul class="sidebar-menu">
                <li class="${empty currentAction || currentAction == 'dashboard' ? 'active' : ''}" id="menu-dashboard">
                    <a href="${pageContext.request.contextPath}/BorrowerDashboardServlet?action=dashboard">📊 Tổng Quan Main</a>
                </li>
                
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

                <li class="${currentAction == 'loan_packages' ? 'active' : ''}">
                    <a href="${pageContext.request.contextPath}/BorrowerDashboardServlet?action=loan_packages">🎁 Các Gói Vay Hệ Thống</a>
                </li>

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
                    <c:when test="${currentAction == 'loan_packages'}">Các Gói Vay Hệ Thống Hỗ Trợ</c:when>
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
            
            <%-- THÔNG BÁO HỆ THỐNG --%>
            <c:if test="${param.msg == 'ekyc_updated_success'}">
                <div class="alert-banner alert-banner-success">
                    <strong>🎉 Thành công:</strong> Hồ sơ eKYC của bạn đã được gửi lại thành công. Trạng thái tài khoản chuyển về <b>Chờ duyệt (Pending)</b>.
                </div>
            </c:if>

            <c:if test="${param.msg == 'loan_submit_success'}">
                <div class="alert-banner alert-banner-success">
                    <strong>🎉 Đăng ký thành công:</strong> Đơn vay đã tiếp nhận sang trạng thái <b>Chờ duyệt</b> để thẩm định tệp hồ sơ CIC PDF.
                </div>
            </c:if>

            <c:if test="${param.msg == 'error_ekyc_rejected' || (trangThaiEkyc == 'rejected' && currentAction != 're_ekyc')}">
                <div class="alert-banner alert-banner-danger" id="rejected-warning-banner">
                    <strong>⚠️ Quyền truy cập bị hạn chế:</strong> Hồ sơ định danh cá nhân (eKYC) của bạn hiện đang ở trạng thái <b>Từ chối (Rejected)</b>.
                    <br>
                    <a href="${pageContext.request.contextPath}/BorrowerDashboardServlet?action=re_ekyc" class="btn-action-ekyc">🔄 Cập nhật lại thông tin eKYC ngay</a>
                </div>
            </c:if>
            
            <c:if test="${param.msg == 'error_already_has_loan' || (hasActiveLoan && (empty currentAction || currentAction == 'dashboard'))}">
                <div class="alert-banner alert-banner-warning">
                    <strong>ℹ️ Thông báo hạn mức:</strong> Bạn đang có một yêu cầu vay đang xử lý hồ sơ hoặc một khoản nợ chưa tất toán hoàn toàn trên hệ thống.
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
                                                                <a href="${myLoan.cicPdfUrl}" target="_blank" style="color:#2563eb; text-decoration:underline; font-weight: bold;">📄 Xem PDF</a>
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

                <%-- TAB ĐẶC BIỆT: CẬP NHẬT LẠI EKYC --%>
                <c:when test="${currentAction == 're_ekyc'}">
                    <div class="data-card" style="max-width: 600px; margin: 0 auto;">
                        <h4>🔄 Làm mới hồ sơ định danh cá nhân (eKYC)</h4>
                        <form action="${pageContext.request.contextPath}/BorrowerDashboardServlet" method="POST" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="update_ekyc">
                            
                            <div class="form-group">
                                <label>Ảnh mặt trước CCCD / CMND <span style="color:red;">*</span></label>
                                <input type="file" name="cccd_front" class="form-control" accept="image/*" required onchange="previewImage(this, 'front_preview')">
                                <div id="front_preview" class="preview-box"><img src="" alt="Preview"></div>
                            </div>

                            <div class="form-group">
                                <label>Ảnh mặt sau CCCD / CMND <span style="color:red;">*</span></label>
                                <input type="file" name="cccd_back" class="form-control" accept="image/*" required onchange="previewImage(this, 'back_preview')">
                                <div id="back_preview" class="preview-box"><img src="" alt="Preview"></div>
                            </div>

                            <div class="form-group">
                                <label>Ảnh chân dung chụp cùng CCCD (Selfie) <span style="color:red;">*</span></label>
                                <input type="file" name="selfie_avatar" class="form-control" accept="image/*" required onchange="previewImage(this, 'selfie_preview')">
                                <div id="selfie_preview" class="preview-box"><img src="" alt="Preview"></div>
                            </div>

                            <div class="form-group">
                                <label>Thu nhập hằng tháng kê khai (VNĐ)</label>
                                <input type="number" name="monthlyIncome" class="form-control" value="${not empty borrowerObj.monthlyIncome ? borrowerObj.monthlyIncome : ''}" required oninput="previewCurrencyUpdate(this.value)">
                                <div id="updateCurrencyPreview" class="currency-preview"></div>
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
                                        <span class="form-hint">Hạn mức tối đa được phép vay: <strong style="color: var(--primary-color);"><fmt:formatNumber value="${hanMucToiDa}" type="number"/> đ</strong></span>
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
                                <p>Tài khoản của bạn hiện chưa xác thực eKYC hoặc đang có một đơn vay khác đang hoạt động.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:when>

                <%-- TAB CHÍNH: HIỂN THỊ CẤU HÌNH 5 GÓI VAY HỆ THỐNG --%>
                <c:when test="${currentAction == 'loan_packages'}">
                    <div class="data-card">
                        <h4>🎁 Danh Sách Gói Sản Phẩm Tín Dụng Hệ Thống</h4>
                        <p style="font-size: 14px; color: #64748b; margin-bottom: 25px;">Hệ thống tự động phê duyệt hạn mức tối đa dựa trên điểm eKYC, lịch sử CIC và nguồn thu nhập thực tế của bạn.</p>
                        
                        <!-- GÓI 1 -->
                        <div class="package-card">
                            <div class="package-header">
                                <span class="package-title">📦 Gói 1: Vay Tiêu Dùng Nhanh (Ứng lương / Mua sắm nhỏ)</span>
                                <span class="package-limit">1.000.000 đ - 15.000.000 đ</span>
                            </div>
                            <div class="package-desc">Dành cho cá nhân cần tiền gấp, duyệt nhanh, hình thức tín chấp qua eKYC cơ bản.</div>
                            <div class="sub-config-title">Các gói cấu hình kỳ hạn nhỏ:</div>
                            <div class="sub-config-list">
                                <div class="sub-config-item">📅 Kỳ hạn 1 tháng: Lãi suất <strong>1.0% / tháng</strong> (12.0% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 3 tháng: Lãi suất <strong>1.2% / tháng</strong> (14.4% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 6 tháng: Lãi suất <strong>1.5% / tháng</strong> (18.0% / năm)</div>
                            </div>
                        </div>

                        <!-- GÓI 2 -->
                        <div class="package-card">
                            <div class="package-header">
                                <span class="package-title">📦 Gói 2: Vay Trả Góp Linh Hoạt (Mua xe, đồ công nghệ, học phí)</span>
                                <span class="package-limit">> 15.000.000 đ - 100.000.000 đ</span>
                            </div>
                            <div class="package-desc">Gói phổ thông, đòi hỏi chứng minh thu nhập tốt. Hình thức tín chấp hoặc thế chấp bằng chính tài sản mua (như ô tô nhỏ).</div>
                            <div class="sub-config-title">Các gói cấu hình kỳ hạn nhỏ:</div>
                            <div class="sub-config-list">
                                <div class="sub-config-item">📅 Kỳ hạn 6 tháng: Lãi suất <strong>0.9% / tháng</strong> (10.8% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 12 tháng: Lãi suất <strong>1.1% / tháng</strong> (13.2% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 18 tháng: Lãi suất <strong>1.3% / tháng</strong> (15.6% / năm)</div>
                            </div>
                        </div>

                        <!-- GÓI 3 -->
                        <div class="package-card">
                            <div class="package-header">
                                <span class="package-title">📦 Gói 3: Vay Kinh Doanh Nhỏ / Hộ Gia Đình</span>
                                <span class="package-limit">> 100.000.000 đ - 500.000.000 đ</span>
                            </div>
                            <div class="package-desc">Dành cho các chủ shop online, hộ kinh doanh cá thể cần nguồn vốn nhập hàng, quay vòng dòng tiền nhanh.</div>
                            <div class="sub-config-title">Các gói cấu hình kỳ hạn nhỏ:</div>
                            <div class="sub-config-list">
                                <div class="sub-config-item">📅 Kỳ hạn 12 tháng: Lãi suất <strong>0.8% / tháng</strong> (9.6% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 24 tháng: Lãi suất <strong>1.0% / tháng</strong> (12.0% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 36 tháng: Lãi suất <strong>1.2% / tháng</strong> (14.4% / năm)</div>
                            </div>
                        </div>

                        <!-- GÓI 4 -->
                        <div class="package-card">
                            <div class="package-header">
                                <span class="package-title">📦 Gói 4: Vay Khởi Nghiệp / Doanh Nghiệp Phát Triển (SME)</span>
                                <span class="package-limit">> 500.000.000 đ - 1.500.000.000 đ</span>
                            </div>
                            <div class="package-desc">Bổ sung vốn lưu động, mua sắm máy móc, thiết bị sản xuất. Yêu cầu có tài sản đảm bảo (máy móc, nhà xưởng, xe tải) hoặc báo cáo tài chính kiểm toán tốt.</div>
                            <div class="sub-config-title">Các gói cấu hình kỳ hạn nhỏ:</div>
                            <div class="sub-config-list">
                                <div class="sub-config-item">📅 Kỳ hạn 12 tháng: Lãi suất <strong>0.75% / tháng</strong> (9.0% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 24 tháng: Lãi suất <strong>0.85% / tháng</strong> (10.2% / năm)</div>
                                <div class="sub-config-item">📅 Kỳ hạn 36 tháng: Lãi suất <strong>0.95% / tháng</strong> (11.4% / năm)</div>
                            </div>
                        </div>

                        <!-- GÓI 5 -->
                        <div class="package-card" style="border-color: #f59e0b; background: #fffbeb;">
                            <div class="package-header">
                                <span class="package-title" style="color: #b45309;">💎 Gói 5: Vay Đầu Tư Bất Động Sản & Dự Án Lớn</span>
                                <span class="package-limit" style="color: #b45309;">Hạn Mức Cao Cấp</span>
                            </div>
                            <div class="package-desc" style="color: #78350f;">Gói vay lớn nhất trên sàn P2P Lending. <b>Bắt buộc phải thế chấp bằng Bất động sản hợp pháp (Sổ đỏ / Sổ hồng)</b>. Quy trình thẩm định hồ sơ thực địa nghiêm ngặt qua nhiều bước bảo mật.</div>
                            <div class="sub-config-title" style="color: #b45309;">Yêu cầu cấu hình:</div>
                            <div class="sub-config-list">
                                <span class="badge badge-warning" style="background:#fef3c7; color:#b45309;">Thẩm định tài sản riêng biệt</span>
                                <span class="badge badge-warning" style="background:#fef3c7; color:#b45309;">Lãi suất thỏa thuận ưu đãi</span>
                            </div>
                        </div>

                    </div>
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
                                    <th>TIẾN ĐỘ GỌI VỐN</th>
                                </tr>
                            </thead>
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
                                        <tr><td colspan="6" style="text-align: center; color: #a0aec0; padding: 20px;">Không có đơn gọi vốn nào khác trên sàn.</td></tr>
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
        window.addEventListener('DOMContentLoaded', () => {
            const urlParams = new URLSearchParams(window.location.search);
            const actionParam = urlParams.get('action');
            if (actionParam === 're_ekyc') {
                const activeMenu = document.querySelector('.sidebar-menu li.active');
                if (activeMenu) activeMenu.classList.remove('active');
            }
        });

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
            }
        }

        function previewCurrency(value) {
            formatVND(value, document.getElementById('currencyPreview'));
        }

        function previewCurrencyUpdate(value) {
            formatVND(value, document.getElementById('updateCurrencyPreview'));
        }

        function formatVND(value, element) {
            if (!value || isNaN(value)) { element.innerText = ''; return; }
            let formatter = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });
            element.innerText = '👉 Quy đổi định dạng: ' + formatter.format(value);
        }

        function validateForm() {
            const inputSotien = document.getElementById('soTienVay');
            const soTienVay = parseFloat(inputSotien.value);
            const hanMucToiDa = parseFloat(inputSotien.getAttribute('data-max')) || 0;
            if (soTienVay > hanMucToiDa) {
                alert("Số tiền đăng ký vay vượt quá hạn mức cho phép (" + hanMucToiDa.toLocaleString('vi-VN') + " đ)!");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>