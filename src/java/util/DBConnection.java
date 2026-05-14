package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try {
            // 1. Khai báo Driver (giúp nhận diện file .jar bạn đã add)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Cấu hình các thông số (Lưu ý: p2p_lending_db)
            String url = "jdbc:mysql://localhost:3306/p2p_lending_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String username = "root"; 
            String password = ""; // Linh kiểm tra lại pass này lần nữa nhé!

            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Không tìm thấy Driver MySQL!");
        }
    }
}