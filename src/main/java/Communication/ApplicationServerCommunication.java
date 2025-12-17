package Communication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import com.google.gson.Gson;

/**
 * アプリケーションサーバ（ゲームサーバ）との通信を担当するクラス
 * マッチング成立時などに、ゲームサーバへ通知を送ります。
 */
public class ApplicationServerCommunication {

    private static final String APP_SERVER_URL = "http://localhost:8081/api/match";

    private Gson gson = new Gson();

    // --- 送信データの構造 (JSON変換用) ---
    // ゲームサーバが受け取る形に合わせて調整してください
    private static class MatchRequest {
        String type = "START_GAME";
        List<String> playerIds; // マッチングしたユーザIDリスト

        MatchRequest(List<String> ids) {
            this.playerIds = ids;
        }
    }

    /**
     * アプリケーションサーバにマッチング成立を通知する
     * @param userIds ゲームに参加するユーザIDのリスト
     */
    public void notifyMatchingRequest(List<String> userIds) {
        // 1. 送信データをJSON文字列に変換
        MatchRequest req = new MatchRequest(userIds);
        String jsonBody = gson.toJson(req);

        // 2. HTTPクライアント作成 (Java 11以降の標準機能)
        HttpClient client = HttpClient.newHttpClient();

        // 3. リクエスト作成 (POSTメソッド)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APP_SERVER_URL)) // URL指定
                .timeout(Duration.ofSeconds(5))  // タイムアウト設定(5秒)
                .header("Content-Type", "application/json") // JSONを送ると宣言
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // データをセット
                .build();

        // 4. 非同期で送信（結果を待たずに処理を戻す）
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    // 送信成功時の処理
                    System.out.println("AppServerへの通知完了: ステータスコード " + response.statusCode());
                })
                .exceptionally(e -> {
                    // 送信失敗時の処理
                    System.err.println("AppServerへの通知失敗: " + e.getMessage());
                    return null;
                });
    }
}