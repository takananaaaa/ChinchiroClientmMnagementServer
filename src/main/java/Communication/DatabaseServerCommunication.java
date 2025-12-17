package Communication;

import java.sql.*;

public class DatabaseServerCommunication {

    // C班用データベース接続情報
    private static final String DB_URL = "jdbc:mysql://sql.yamazaki.se.shibaura-it.ac.jp:13308/db_group_c";
    private static final String USER = "group_c";
    private static final String PASS = "group_c";

    /**
     * ログイン認証を行う
     * @return 認証成功ならtrue
     */
    public boolean login(String id, String password) {
        String sql = "SELECT id FROM users WHERE id = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
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
     * ユーザ情報を取得する（ログイン成功後に呼ぶ想定）
     * @return UserDataオブジェクト（見つからない場合はnull）
     */
    public UserData getUserData(String id) {
        String sql = "SELECT name, bananas FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("name"), rs.getInt("bananas"));
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
    public boolean registerUser(String id, String name, String password) {
        // 初期バナナ100本
        String sql = "INSERT INTO users (id, name, password, bananas) VALUES (?, ?, ?, 100)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, password);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB登録エラー: " + e.getMessage()); // 重複エラーなどがわかるように
            return false;
        }
    }

    // データ受け渡し用の簡易クラス
    public static class UserData {
        public String name;
        public int bananas;
        public UserData(String name, int bananas) {
            this.name = name;
            this.bananas = bananas;
        }
    }
}