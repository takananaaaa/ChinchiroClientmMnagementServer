import org.glassfish.tyrus.server.Server;
import Communication.ClientCommunication;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunServer {

    public static void main(String[] args) {
        // 設定: localhost の 8080 ポートで起動
        // コンテキストパス: /ChinchiroServer
        // エンドポイント: ClientCommunicationクラス (@ServerEndpoint("/ws")がついている)
        Server server = new Server("localhost", 8080, "/ChinchiroServer",null, ClientCommunication.class);

        try {
            server.start();
            System.out.println("--- ユーザ管理サーバが起動しました ---");
            System.out.println("URL: ws://localhost:8080/ChinchiroServer/ws");
            System.out.println("停止するにはEnterキーを押してください...");

            // 入力待ちで停止を防ぐ
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
            System.out.println("サーバを停止しました。");
        }
    }
}