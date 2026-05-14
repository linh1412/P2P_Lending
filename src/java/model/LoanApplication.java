package model;

import java.sql.Date;
import java.sql.Timestamp;

public class LoanApplication {
    private long applicationId;
    private long borrowerId;
    private double amountRequested;
    private int termMonths;
    private String status;
    private Date cicIssuedDate;
    private String cicPdfUrl;
    private Timestamp createdAt;

    public LoanApplication() {}

    // Constructor để tạo đơn vay mới
    public LoanApplication(long borrowerId, double amountRequested, int termMonths, Date cicIssuedDate, String cicPdfUrl) {
        this.borrowerId = borrowerId;
        this.amountRequested = amountRequested;
        this.termMonths = termMonths;
        this.cicIssuedDate = cicIssuedDate;
        this.cicPdfUrl = cicPdfUrl;
    }

    // Getter và Setter chuẩn ký hiệu SQL
    public long getApplicationId() { return applicationId; }
    public void setApplicationId(long applicationId) { this.applicationId = applicationId; }

    public long getBorrowerId() { return borrowerId; }
    public void setBorrowerId(long borrowerId) { this.borrowerId = borrowerId; }

    public double getAmountRequested() { return amountRequested; }
    public void setAmountRequested(double amountRequested) { this.amountRequested = amountRequested; }

    public int getTermMonths() { return termMonths; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCicIssuedDate() { return cicIssuedDate; }
    public void setCicIssuedDate(Date cicIssuedDate) { this.cicIssuedDate = cicIssuedDate; }

    public String getCicPdfUrl() { return cicPdfUrl; }
    public void setCicPdfUrl(String cicPdfUrl) { this.cicPdfUrl = cicPdfUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}