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
    // 各種処理クラス
    private NewRegistration newRegCtrl;
    private Login loginCtrl;
    private MatchingSystem matchingCtrl;

    /**
     * コンストラクタ
     */
    public ClientManagementController() {
        // 通信クラス(communicationCtrl)はここでは生成せず、setterで受け取る
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
                // REGISTER,userName,password
                if (parts.length >= 3) {
                    // 受信した情報でNewRegistrationインスタンスを生成
                    this.newRegCtrl = new NewRegistration(parts[1], parts[2]);
                    processNewRegistration();
                }
                break;

            case "MATCHING":
                // MATCHING,userName
                if (parts.length >= 2) {
                    notifyMatchingRequest(parts[1]);
                }
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
        try {
            // Loginクラス内で入力チェックとDB認証を行う
            boolean isAuthenticated = loginCtrl.login();

            if (isAuthenticated) {
                String userName = loginCtrl.getUserName();
                System.out.println("ログイン成功: " + userName);
                communicationCtrl.sendLoginSuccess(userName);
            } else {
                System.out.println("ログイン失敗: " + loginCtrl.getUserName());
                communicationCtrl.sendLoginFailure();
            }
        } catch (IllegalStateException e) {
            // 入力不備の場合
            System.out.println("ログイン処理エラー: " + e.getMessage());
            communicationCtrl.sendLoginFailure();
        } catch (Exception e) {
            e.printStackTrace();
            communicationCtrl.sendLoginFailure();
        }
    }

    /**
     * 新規登録処理
     */
    public void processNewRegistration() {
        if (newRegCtrl == null) {
            communicationCtrl.sendRegistrationFailure("Internal Error");
            return;
        }

        try {
            // NewRegistrationクラス内で入力チェックとDB登録を行う
            boolean success = newRegCtrl.registerToDatabase();

            if (success) {
                System.out.println("新規登録成功: " + newRegCtrl.getUserName());
                communicationCtrl.sendRegistrationSuccess();
            } else {
                System.out.println("新規登録失敗: DBエラーまたは重複");
                communicationCtrl.sendRegistrationFailure("Database Error or Duplicate");
            }

        } catch (IllegalStateException e) {
            System.out.println("新規登録失敗: 入力不備 - " + e.getMessage());
            communicationCtrl.sendRegistrationFailure("Invalid Input");
        } catch (Exception e) {
            e.printStackTrace();
            communicationCtrl.sendRegistrationFailure("Server Error");
        }
    }

    /**
     * マッチングリクエスト処理
     */
    public void notifyMatchingRequest(String userName) {
        System.out.println("マッチングリクエスト受信: " + userName);
        // マッチングシステムへユーザを追加
        matchingCtrl.addUser(userName);
        // クライアントへマッチング状態を通知
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
