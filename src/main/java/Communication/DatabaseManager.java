package Communication;

import java.sql.*;

public class DatabaseManager {
    private static final String sqlDriverName = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://sql.yamazaki.se.shibaura-it.ac.jp";
    private static final String sqlServerPort = "13308";
    private static final String sqlDatabaseName = "db_group_c";
    private static final String sqlUserId   = "group_c";
    private static final String sqlPassword = "group_c";

    public DatabaseManager() {
        try {
            Class.forName(sqlDriverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() throws SQLException {
        // ポート番号が含まれているので、urlの末尾に「:」を足さないように調整
        String target = url + ":" + sqlServerPort + "/" + sqlDatabaseName;
        // もし url 変数が "jdbc:mysql://hostname" 形式ならこれでOK
        return DriverManager.getConnection(target, sqlUserId, sqlPassword);
    }

    // DatabaseManager.java 内
    public void setupTable() {
        // 1. 既存のテーブルを完全に削除するSQL
        String dropSql = "DROP TABLE IF EXISTS users";

        // 2. 新しい定義でテーブルを作成するSQL（nameカラムを使用）
        String createSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(50) NOT NULL"
                + ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // テーブルを削除（リセット実行）普段はコメントアウト。何か起きたときに
            //stmt.executeUpdate(dropSql);
            //sSystem.out.println("既存のusersテーブルを削除しました。");

            // テーブルを再作成
            stmt.executeUpdate(createSql);
            System.out.println("usersテーブルを新規作成しました。");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}