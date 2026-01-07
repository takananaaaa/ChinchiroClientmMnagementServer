package Communication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseServerCommunication {

    private DatabaseManager dbManager = new DatabaseManager();

    /**
     * ログイン認証を行う (name列を使用)
     */
    public boolean login(String name, String password) {
        // ここが username になっているとエラーになります。必ず name にしてください。
        String sql = "SELECT name FROM users WHERE name = ? AND password = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 新規登録を行う (name列を使用)
     */
    public boolean registerUser(String name, String password) {
        // ここも username ではなく name に修正します。
        String sql = "INSERT INTO users (name, password) VALUES (?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, password);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB登録エラー: " + e.getMessage());
            return false;
        }
    }

    /**
     * ユーザ情報を取得する
     */
    public UserData getUserData(String name) {
        String sql = "SELECT name FROM users WHERE name = ?";

        try (Connection conn = dbManager.getConnection();
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

    public static class UserData {
        public String name;
        public UserData(String name) {
            this.name = name;
        }
    }
}