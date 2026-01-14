package Control;

import Communication.ClientCommunication;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientManagementController {

    private ClientCommunication communicationCtrl;

    // 既存の機能（DB処理など）はそのまま保持
    private NewRegistration newRegCtrl;
    private Login loginCtrl;

    // ★修正1: マッチングシステムは「全ユーザーで共有」するため static final にする
    // これにより、誰が接続しても「同じ1つの待ち行列」を使えるようになります
    private static final MatchingSystem matchingCtrl = new MatchingSystem();

    // Gson
    private Gson gson = new Gson();

    public ClientManagementController() {
        this.loginCtrl = new Login();
        // matchingCtrl は static で初期化済みなのでここでは new しない
    }

    public void setCommunicationController(ClientCommunication communicationCtrl) {
        this.communicationCtrl = communicationCtrl;
    }

    /**
     * JSONメッセージを受信して処理を振り分ける
     */
    public void receiveMessage(String message) {
        if (message == null || message.isEmpty()) return;

        System.out.println("受信データ: " + message);

        try {
            // 1. JSONとしてパース
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();

            // 2. typeが含まれていなければ無視
            if (!json.has("type")) return;
            String requestType = json.get("type").getAsString();

            switch (requestType) {
                case "LOGIN":
                    if (json.has("id") && json.has("password")) {
                        String id = json.get("id").getAsString();
                        String pass = json.get("password").getAsString();
                        loginCtrl.setLoginInfo(id, pass);
                        processLogin();
                    }
                    break;

                case "RESISTER":
                case "REGISTER":
                    if (json.has("id") && json.has("pass")) {
                        String id = json.get("id").getAsString();
                        String pass = json.get("pass").getAsString();
                        this.newRegCtrl = new NewRegistration(id, pass);
                        processNewRegistration();
                    }
                    break;

                case "MATCHING":
                    if (json.has("id")) {
                        notifyMatchingRequest(json.get("id").getAsString());
                    }
                    break;

                // ★追加: ログアウト時の待機列削除対応
                case "LOGOUT":
                    if (json.has("id")) {
                        String id = json.get("id").getAsString();
                        matchingCtrl.removeUser(id);
                    }
                    processLogout();
                    break;

                default:
                    System.out.println("不明なリクエスト: " + requestType);
                    break;
            }
        } catch (Exception e) {
            System.err.println("JSONパースエラー: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ログイン処理（既存のまま）
     */
    public void processLogin() {
        try {
            boolean isAuthenticated = loginCtrl.login();

            if (isAuthenticated) {
                String userName = loginCtrl.getUserName();
                System.out.println("ログイン成功: " + userName);
                communicationCtrl.sendLoginSuccess(userName);
            } else {
                System.out.println("ログイン失敗: " + loginCtrl.getUserName());
                communicationCtrl.sendLoginFailure();
            }
        } catch (Exception e) {
            e.printStackTrace();
            communicationCtrl.sendLoginFailure();
        }
    }

    /**
     * 新規登録処理（既存のまま）
     */
    public void processNewRegistration() {
        if (newRegCtrl == null) {
            communicationCtrl.sendRegistrationFailure("Internal Error");
            return;
        }

        try {
            boolean success = newRegCtrl.registerToDatabase();

            if (success) {
                System.out.println("新規登録成功: " + newRegCtrl.getUserName());
                communicationCtrl.sendRegistrationSuccess();
            } else {
                System.out.println("新規登録失敗");
                communicationCtrl.sendRegistrationFailure("Database Error or Duplicate");
            }
        } catch (Exception e) {
            e.printStackTrace();
            communicationCtrl.sendRegistrationFailure("Server Error");
        }
    }

    /**
     * マッチングリクエスト処理（★修正）
     */
    public void notifyMatchingRequest(String userName) {
        System.out.println("マッチングリクエスト受信: " + userName);

        // ★修正2: 名前と一緒に「通信機能(communicationCtrl)」を渡す
        // これで MatchingSystem から「マッチング成立！」の連絡を送れるようになる
        if (communicationCtrl != null) {
            matchingCtrl.addUser(userName, this.communicationCtrl);
        }
    }

    /**
     * ログアウト処理
     */
    public void processLogout() {
        System.out.println("ログアウト処理");
        if (communicationCtrl != null) {
            communicationCtrl.sendLogoutResponse();
        }
    }
}