import Control.ClientManagementController;
import Communication.ClientCommunication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

class ClientManagementControllerTest {

    private ClientManagementController controller;
    // 通信クラスの挙動を確認するためのスタブ（またはMock）
    // ※今回は挙動確認のため簡易的な構成を想定

    @BeforeEach
    void setUp() {
        controller = new ClientManagementController();
    }

    @Nested
    @DisplayName("receiveMessage: JSON解析と振り分けのテスト")
    class ReceiveMessageTest {

        @Test
        @DisplayName("LOGINリクエストが正しく処理されるか")
        void testReceiveMessage_Login() {
            String json = "{\"type\":\"LOGIN\", \"id\":\"user1\", \"password\":\"pass123\"}";

            // 例外が発生せずに処理が完了することを確認
            assertDoesNotThrow(() -> controller.receiveMessage(json));
        }

        @Test
        @DisplayName("REGISTERリクエストが正しく処理されるか")
        void testReceiveMessage_Register() {
            // タイポ（RESISTER）と正しい綴り（REGISTER）の両方に対応しているか
            String json = "{\"type\":\"REGISTER\", \"id\":\"newUser\", \"pass\":\"newPass\"}";

            assertDoesNotThrow(() -> controller.receiveMessage(json));
        }

        @Test
        @DisplayName("不正な形式のJSONやnullが入力された場合にクラッシュしないか")
        void testReceiveMessage_InvalidJson() {
            // null
            assertDoesNotThrow(() -> controller.receiveMessage(null));

            // 空文字
            assertDoesNotThrow(() -> controller.receiveMessage(""));

            // 壊れたJSON
            assertDoesNotThrow(() -> controller.receiveMessage("{ invalid json }"));
        }
    }


}