package Communication;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApplicationServerCommunication {

    // APサーバの登録用URL (RunGameServerの設定に合わせてポート8081)
    private static final String AP_SERVER_URL = "http://localhost:8081/GameServer/players/register";
    private final Gson gson = new Gson();

    // APサーバに合わせて送るデータ構造
    private static class PlayerInfoDto {
        String name;
        PlayerInfoDto(String name) { this.name = name; }
    }

    /**
     * マッチング成立したユーザリストをAPサーバへ通知する
     * @param userIds 成立したユーザIDのリスト
     * @return 送信成功ならtrue
     */
    public boolean notifyMatchingRequest(List<String> userIds) {
        System.out.println("[AppServerComm] Sending players to AP Server: " + userIds);

        try {
            // 1. データ変換 (Stringリスト -> PlayerInfoDtoのリスト)
            List<PlayerInfoDto> dtos = new ArrayList<>();
            for (String id : userIds) {
                dtos.add(new PlayerInfoDto(id));
            }
            String jsonBody = gson.toJson(dtos);

            // 2. HTTP接続設定
            URL url = new URL(AP_SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true); // ボディ送信有効化

            // 3. 送信
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 4. 結果確認
            int responseCode = conn.getResponseCode();
            System.out.println("[AppServerComm] Response Code: " + responseCode);

            // 204 No Content (成功) または 200 OK
            return (responseCode >= 200 && responseCode < 300);

        } catch (Exception e) {
            System.err.println("[AppServerComm] Error sending request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}