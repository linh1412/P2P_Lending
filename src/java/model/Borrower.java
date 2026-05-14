package model;

public class Borrower {
    private long borrowerId;
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private String verificationStatus;
    private double monthlyIncome;
    private String idImageUrl; // Vẫn giữ để code Controller không lỗi

    public Borrower() {}

    public Borrower(long borrowerId, String firstName, String lastName, String idCardNumber, double monthlyIncome) {
        this.borrowerId = borrowerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCardNumber = idCardNumber;
        this.monthlyIncome = monthlyIncome;
    }

    // Getter/Setter chuẩn
    public long getBorrowerId() { return borrowerId; }
    public void setBorrowerId(long borrowerId) { this.borrowerId = borrowerId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getIdCardNumber() { return idCardNumber; }
    public void setIdCardNumber(String idCardNumber) { this.idCardNumber = idCardNumber; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public String getIdImageUrl() { return idImageUrl; }
    public void setIdImageUrl(String idImageUrl) { this.idImageUrl = idImageUrl; }

    // Hỗ trợ gọi từ JSP cũ
    public String getFirst_name() { return firstName; }
    public String getLast_name() { return lastName; }
    public String getId_card_number() { return idCardNumber; }
    public String getId_image_url() { return idImageUrl; }
}