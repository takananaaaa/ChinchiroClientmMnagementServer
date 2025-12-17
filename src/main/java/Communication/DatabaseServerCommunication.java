package Communication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseServerCommunication {

    private static final String DB_URL =
            "jdbc:mysql://sql.yamazaki.se.shibaura-it.ac.jp:13308/db_group_c";
    private static final String USER = "group_c";
    private static final String PASS = "group_c";

    // ログイン認証（userNameで判断）
    public boolean login(String userName, String password) {
        String sql = "SELECT name FROM users WHERE name = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userName);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ユーザ情報取得
    public UserData getUserData(String userName) {
        String sql = "SELECT bananas FROM users WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(userName, rs.getInt("bananas"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新規登録（idなし）
    public boolean registerUser(String userName, String password) {
        String sql =
                "INSERT INTO users (name, password, bananas) VALUES (?, ?, 100)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userName);
            pstmt.setString(2, password);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB登録エラー: " + e.getMessage());
            return false;
        }
    }

    // ユーザ情報クラス
    public static class UserData {
        public String userName;
        public int bananas;

        public UserData(String userName, int bananas) {
            this.userName = userName;
            this.bananas = bananas;
        }
    }
}
