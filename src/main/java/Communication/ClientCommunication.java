package Communication;

import Control.ClientManagementController;
import com.google.gson.Gson;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

// WebSocketのエンドポイントとして動作させるためのアノテーション
@ServerEndpoint("/ws")
public class ClientCommunication {

    // この接続に関連するセッション
    private Session session;

    // この接続を担当するコントローラ
    private ClientManagementController managementController;

    private Gson gson = new Gson();

    /**
     * 接続確立時に呼ばれる
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("接続開始: " + session.getId());

        // ★重要: ここで new しているので、ユーザごとに別のコントローラーが作られます。
        // MatchingSystem (待機リスト) は static フィールドなどで共有するように
        // ClientManagementController 側で実装してください。
        this.managementController = new ClientManagementController();

        // コントローラー側からこの通信クラスを呼べるようにセットする
        // (ClientManagementController側に setCommunicationController メソッドが必要です)
        this.managementController.setCommunicationController(this);
    }

    /**
     * メッセージ受信時に呼ばれる
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("受信: " + message);
        if (managementController != null) {
            // 受信したJSON文字列をそのままコントローラへ渡す
            managementController.receiveMessage(message);
        }
    }

    /**
     * 切断時に呼ばれる
     */
    @OnClose
    public void onClose() {
        System.out.println("接続終了: " + (session != null ? session.getId() : "unknown"));
        // 必要ならコントローラーに切断を通知する処理を追加
    }

    /**
     * エラー時に呼ばれる
     */
    @OnError
    public void onError(Throwable error) {
        System.err.println("通信エラー: " + error.getMessage());
    }

    // --- コントローラから呼び出される送信メソッド ---

    /**
     * ログイン成功を通知する
     * ★修正: バナナ数も送れるように引数を追加しました
     */
    public void sendLoginSuccess(String userName, int bananas) {
        ResponseMessage response = new ResponseMessage("LOGIN_SUCCESS", true, "ログイン成功");
        response.userName = userName;
        response.bananas = bananas; // バナナ数をセット
        sendMessage(response);
    }

    /**
     * ログイン失敗を通知する
     */
    public void sendLoginFailure() {
        ResponseMessage response = new ResponseMessage("LOGIN_FAILURE", false, "ログイン失敗");
        sendMessage(response);
    }

    /**
     * 新規登録成功を通知する
     */
    public void sendRegistrationSuccess() {
        ResponseMessage response = new ResponseMessage("REGISTER_SUCCESS", true, "新規登録成功");
        sendMessage(response);
    }

    /**
     * 新規登録失敗を通知する
     */
    public void sendRegistrationFailure(String reason) {
        ResponseMessage response = new ResponseMessage("REGISTER_FAILURE", false, reason);
        sendMessage(response);
    }

    /**
     * ログアウト完了を通知する
     */
    public void sendLogoutResponse() {
        ResponseMessage response = new ResponseMessage("LOGOUT_SUCCESS", true, "ログアウトしました");
        sendMessage(response);
    }

    /**
     * マッチング等の通知を送る
     */
    public void sendMatchResponse(String statusMessage) {
        ResponseMessage response = new ResponseMessage("MATCH_STATUS", true, statusMessage);
        sendMessage(response);
    }

    // --- 内部処理 ---

    /**
     * JSON送信の共通処理
     */
    private void sendMessage(Object messageObj) {
        if (this.session != null && this.session.isOpen()) {
            try {
                String json = gson.toJson(messageObj);
                this.session.getBasicRemote().sendText(json);
                System.out.println("送信: " + json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 送信データのフォーマット（JSON変換用）
    private static class ResponseMessage {
        String type;
        boolean result;
        String message;
        String userName;
        int bananas; // ★ログイン成功時に使用

        ResponseMessage(String type, boolean result, String message) {
            this.type = type;
            this.result = result;
            this.message = message;
        }
    }
}