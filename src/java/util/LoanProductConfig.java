package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoanProductConfig {

    // Danh sách các gói vay và cấu hình con
    public static final List<Product> PRODUCTS = Arrays.asList(
        new Product(1, "Vay Tiêu Dùng Nhanh", 1000000, 15000000, Arrays.asList(
            new Config(1, 1.0), new Config(3, 1.2), new Config(6, 1.5)
        )),
        new Product(2, "Vay Trả Góp Linh Hoạt", 15000001, 100000000, Arrays.asList(
            new Config(6, 0.9), new Config(12, 1.1), new Config(18, 1.3)
        )),
        new Product(3, "Vay Kinh Doanh Nhỏ", 100000001, 500000000, Arrays.asList(
            new Config(12, 0.8), new Config(24, 1.0), new Config(36, 1.2)
        )),
        new Product(4, "Vay Khởi Nghiệp SME", 500000001, 1500000000, Arrays.asList(
            new Config(12, 0.75), new Config(24, 0.85), new Config(36, 0.95)
        )),
        new Product(5, "Vay Đầu Tư Bất Động Sản", 1500000001, Double.MAX_VALUE, new ArrayList<>())
    );

    // Class đại diện cho Gói vay lớn
    public static class Product {
        public int id;
        public String name;
        public double minAmount;
        public double maxAmount;
        public List<Config> configs;

        public Product(int id, String name, double min, double max, List<Config> configs) {
            this.id = id; this.name = name; this.minAmount = min; 
            this.maxAmount = max; this.configs = configs;
        }
    }

    // Class đại diện cho Kỳ hạn và Lãi suất
    public static class Config {
        public int termMonths;
        public double interestRate;

        public Config(int term, double rate) {
            this.termMonths = term; this.interestRate = rate;
        }
    }

    // Hàm tiện ích để tìm gói phù hợp dựa trên số tiền
    public static Product findProductByAmount(double amount) {
        return PRODUCTS.stream()
            .filter(p -> amount >= p.minAmount && amount <= p.maxAmount)
            .findFirst()
            .orElse(null);
    }
}