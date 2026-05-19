package model;

public class Borrower {
    private long borrowerId;
    private String firstName;
    private String lastName;
    private String idCardNumber;
    private String verificationStatus;
    private double monthlyIncome;
    private String idImageUrl; // Vẫn giữ để code Controller không lỗi
    
    // BỔ SUNG: Thuộc tính ví tiền để đồng bộ với cơ sở dữ liệu p2p_lending_db
    private double walletBalance; 

    public Borrower() {}

    public Borrower(long borrowerId, String firstName, String lastName, String idCardNumber, double monthlyIncome) {
        this.borrowerId = borrowerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCardNumber = idCardNumber;
        this.monthlyIncome = monthlyIncome;
    }
    
    // BỔ SUNG: Constructor đầy đủ thuộc tính bao gồm cả walletBalance phòng khi cần dùng
    public Borrower(long borrowerId, String firstName, String lastName, String idCardNumber, String verificationStatus, double monthlyIncome, String idImageUrl, double walletBalance) {
        this.borrowerId = borrowerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCardNumber = idCardNumber;
        this.verificationStatus = verificationStatus;
        this.monthlyIncome = monthlyIncome;
        this.idImageUrl = idImageUrl;
        this.walletBalance = walletBalance;
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
    
    // BỔ SUNG: Getter/Setter chuẩn cho walletBalance giúp sửa lỗi bên BorrowerDAO
    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    // Hỗ trợ gọi từ JSP cũ (Các hàm viết theo snake_case để tránh lỗi giao diện)
    public String getFirst_name() { return firstName; }
    public String getLast_name() { return lastName; }
    public String getId_card_number() { return idCardNumber; }
    public String getId_image_url() { return idImageUrl; }
    
    // BỔ SUNG: Hỗ trợ gọi ${borrower.wallet_balance} trực tiếp từ file JSP cũ nếu có
    public double getWallet_balance() { return walletBalance; }
}