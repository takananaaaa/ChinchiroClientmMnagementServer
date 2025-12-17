package Communication;

import javax.websocket.Session;
import com.google.gson.Gson;
import java.io.IOException;

public class ClientCommunication {

    private Gson gson = new Gson();

    // 送信データのフォーマット（内部クラス）
    // ※後で他の人と共通化するなら別ファイルに切り出してもOK
    private static class ResponseMessage {
        String type;        // "LOGIN_RESULT" など
        boolean result;     // 成功:true, 失敗:false
        String message;     // エラーメッセージなど
        String userName;    // ログイン成功時用
        int bananas;        // ログイン成功時用

        // コンストラクタ
        ResponseMessage(String type, boolean result, String message) {
            this.type = type;
            this.result = result;
            this.message = message;
        }
    }

    /**
     * JSON送信の共通処理
     */
    private void sendMessage(Session session, Object messageObj) {
        if (session != null && session.isOpen()) {
            try {
                String json = gson.toJson(messageObj);
                session.getBasicRemote().sendText(json);
                System.out.println("送信(Clientへ): " + json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // --- 以下、設計書に基づいたメソッド ---

    /**
     * ログイン結果を送信する
     * @param session クライアントのセッション
     * @param result 成功ならtrue
     * @param message 表示メッセージ
     * @param userName ユーザ名 (失敗時はnull可)
     * @param bananas 所持バナナ数 (失敗時は0可)
     */
    public void sendLoginResponse(Session session, boolean result, String message, String userName, int bananas) {
        ResponseMessage response = new ResponseMessage("LOGIN_RESULT", result, message);
        if (result) {
            response.userName = userName;
            response.bananas = bananas;
        }
        sendMessage(session, response);
    }

    /**
     * 新規登録結果を送信する
     */
    public void sendSignUpResponse(Session session, boolean result, String message) {
        ResponseMessage response = new ResponseMessage("SIGNUP_RESULT", result, message);
        sendMessage(session, response);
    }

    /**
     * ログアウト結果を送信する
     */
    public void sendLogoutResponse(Session session, boolean result) {
        ResponseMessage response = new ResponseMessage("LOGOUT_RESULT", result, "Logout processed");
        sendMessage(session, response);
    }

    /**
     * マッチング待機・結果などの通知を送る
     */
    public void sendMatchResponse(Session session, String statusMessage) {
        // 必要に応じてtypeを "MATCH_WAIT" や "MATCH_SUCCESS" に変える
        ResponseMessage response = new ResponseMessage("MATCH_STATUS", true, statusMessage);
        sendMessage(session, response);
    }
}