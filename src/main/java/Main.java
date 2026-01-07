import Communication.DatabaseManager;
import Control.NewRegistration;
import Control.Login;

public class Main {
    public static void main(String[] args) {
        // 1. データベースの初期化
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.setupTable();

        System.out.println("=== 認証テスト開始 ===");

        // テスト用のユーザー情報
        String testUser = "testUser_" + System.currentTimeMillis(); // 重複を避けるため実行時間を付与
        String testPass = "password123";

        try {
            // 2. 新規登録のテスト
            System.out.println("\n[テスト1] 新規登録を試行します: " + testUser);
            NewRegistration reg = new NewRegistration(testUser, testPass);
            boolean regResult = reg.registerToDatabase();

            if (regResult) {
                System.out.println("-> 新規登録成功！");
            } else {
                System.out.println("-> 新規登録失敗（ユーザーが既に存在する可能性があります）");
            }

            // 3. ログインのテスト
            System.out.println("\n[テスト2] ログインを試行します: " + testUser);
            Login login = new Login();
            login.setLoginInfo(testUser, testPass);
            boolean loginResult = login.login();

            if (loginResult) {
                System.out.println("-> ログイン成功！");
            } else {
                System.out.println("-> ログイン失敗（IDまたはパスワードが違います）");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n=== 認証テスト完了 ===");
    }
}


