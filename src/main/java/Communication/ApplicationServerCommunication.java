package Communication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import com.google.gson.Gson;

public class ApplicationServerCommunication {

    // アプリケーションサーバのURL（後で正しいものに変更してください）
    private static final String APP_SERVER_URL = "http://localhost:8081/api/match";
    private Gson gson = new Gson();

    // 送信データ構造
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
        // JSON作成
        MatchRequest req = new MatchRequest(userIds);
        String jsonBody = gson.toJson(req);

        // HTTPクライアント作成
        HttpClient client = HttpClient.newHttpClient();

        // リクエスト作成
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(APP_SERVER_URL))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // 非同期で送信（結果を待たずに処理を戻す）
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("AppServerへの通知完了: " + response.statusCode());
                })
                .exceptionally(e -> {
                    System.err.println("AppServerへの通知失敗: " + e.getMessage());
                    return null;
                });
    }
}