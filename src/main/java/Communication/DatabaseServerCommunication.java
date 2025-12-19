package Communication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseServerCommunication {

    // C班用データベース接続情報
    private static final String DB_URL = "jdbc:mysql://sql.yamazaki.se.shibaura-it.ac.jp:13308/db_group_c";
    private static final String USER = "group_c";
    private static final String PASS = "group_c";

    /**
     * ログイン認証を行う (ユーザ名で判断)
     */
    public boolean login(String name, String password) {
        String sql = "SELECT name FROM users WHERE name = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // レコードがあれば成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ユーザ情報を取得する
     * 更新を行わないため、常にDBの初期値（または固定値）が返されます
     */
    public UserData getUserData(String name) {
        String sql = "SELECT bananas FROM users WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新規登録を行う
     */
    public boolean registerUser(String name, String password) {
        // 初期バナナ100本でINSERT
        String sql = "INSERT INTO users (name, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, password);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB登録エラー: " + e.getMessage());
            return false;
        }
    }

    // データ受け渡し用の簡易クラス
    public static class UserData {
        public String name;
        public UserData(String name) {
            this.name = name;
        }
    }
}