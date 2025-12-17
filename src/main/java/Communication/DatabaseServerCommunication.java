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
     * @param name ユーザ名 (Unique)
     * @param password パスワード
     * @return 認証成功ならtrue
     */
    public boolean login(String name, String password) {
        // IDではなく name で検索するように変更
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
     * ユーザ情報を取得する (ユーザ名で検索)
     * @param name ユーザ名
     * @return UserDataオブジェクト（見つからない場合はnull）
     */
    public UserData getUserData(String name) {
        // 名前を元にバナナ数を取得
        String sql = "SELECT bananas FROM users WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 名前は引数のものをそのまま使い、DBからはバナナ数を取得
                    return new UserData(name, rs.getInt("bananas"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新規登録を行う
     * ※DBの構造上ID(学籍番号)が必要なためIDも受け取りますが、
     * 「名前が重複しない」運用であればDB側でnameにUNIQUE制約を推奨
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
            System.err.println("DB登録エラー: " + e.getMessage());
            // 名前重複エラー(UNIQUE制約がある場合)などはここで検知可能
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