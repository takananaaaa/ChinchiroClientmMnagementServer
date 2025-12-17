package Control;

import Communication.ClientCommunication;
import Communication.DatabaseServerCommunication;

/**
 * クライアント管理コントローラ
 */
public class ClientManagementController {

    // --- 属性 ---

    // 通信を担当するコントローラ (ClientCommunicationからセットされる)
    private ClientCommunication communicationCtrl;

    // データベース通信を担当するコントローラ
    private DatabaseServerCommunication dbCtrl;

    // 各種処理クラス
    private NewRegistration newRegCtrl;
    private Login loginCtrl;
    private MatchingSystem matchingCtrl;

    /**
     * コンストラクタ
     */
    public ClientManagementController() {
        // 通信クラス(communicationCtrl)はここでは生成せず、setterで受け取る
        this.dbCtrl = new DatabaseServerCommunication();
        this.newRegCtrl = new NewRegistration();
        this.loginCtrl = new Login();
        this.matchingCtrl = new MatchingSystem();
    }

    /**
     * 通信コントローラをセットする
     * (ClientCommunicationのonOpenで呼ばれる)
     */
    public void setCommunicationController(ClientCommunication communicationCtrl) {
        this.communicationCtrl = communicationCtrl;
    }

    // --- 操作 ---

    /**
     * クライアントからのメッセージを受信して振り分ける
     */
    public void receiveMessage(String message) {
        if (message == null || message.isEmpty()) return;

        String[] parts = message.split(",");
        String requestType = parts[0];

        switch (requestType) {
            case "LOGIN":
                if (parts.length >= 3) {
                    loginCtrl.setLoginInfo(parts[1], parts[2]);
                    processLogin();
                }
                break;

            case "REGISTER":
                if (parts.length >= 3) {
                    newRegCtrl.setRegistrationInfo(parts[1], parts[2]);
                    processNewRegistration();
                }
                break;

            case "MATCHING":
                notifyMatchingRequest();
                break;

            case "LOGOUT":
                processLogout();
                break;

            default:
                System.out.println("不明なリクエスト: " + requestType);
                break;
        }
    }

    /**
     * ログイン処理
     */
    public void processLogin() {
        if (!loginCtrl.checkInput()) {
            communicationCtrl.sendLoginFailure();
            return;
        }

        String userName = loginCtrl.getUserName();
        String password = loginCtrl.getPassword();

        // DBで認証 (DatabaseServerCommunication.loginを使用)
        boolean isAuthenticated = dbCtrl.login(userName, password);

        if (isAuthenticated) {
            // ログイン成功時、ユーザデータ(バナナ数など)を取得
            DatabaseServerCommunication.UserData userData = dbCtrl.getUserData(userName);

            System.out.println("ログイン成功: " + userName);
            communicationCtrl.sendLoginSuccess(userName);
        } else {
            System.out.println("ログイン失敗: " + userName);
            communicationCtrl.sendLoginFailure();
        }
    }

    /**
     * 新規登録処理
     */
    public void processNewRegistration() {
        /*if (!newRegCtrl.checkInput()) {
            communicationCtrl.sendRegistrationFailure("Invalid Input");
            return;
        }
        */

        String newUserName = newRegCtrl.getUserName();
        String newPassword = newRegCtrl.getPassword();

        // 重複チェック (getUserDataでデータが取れたら既に存在する)
        DatabaseServerCommunication.UserData existingUser = dbCtrl.getUserData(newUserName);

        if (existingUser != null) {
            System.out.println("新規登録失敗: 重複ユーザ " + newUserName);
            communicationCtrl.sendRegistrationFailure("Duplicate Username");
        } else {
            // DBへ登録 (IDとNameは同じものとして扱う)
            boolean success = dbCtrl.registerUser(newUserName, newUserName, newPassword);

            if (success) {
                System.out.println("新規登録成功: " + newUserName);
                communicationCtrl.sendRegistrationSuccess();
            } else {
                System.out.println("新規登録失敗: DBエラー");
                communicationCtrl.sendRegistrationFailure("Database Error");
            }
        }
    }

    /**
     * マッチングリクエスト処理
     */
    public void notifyMatchingRequest() {
        System.out.println("マッチングリクエスト受信");
        // マッチングロジックへ (実装時はユーザ名などを渡す必要あり)
        // matchingCtrl.addUser(...);
        communicationCtrl.sendMatchResponse("WAITING");
    }

    /**
     * ログアウト処理
     */
    public void processLogout() {
        System.out.println("ログアウト処理");
        communicationCtrl.sendLogoutResponse();
    }
}
