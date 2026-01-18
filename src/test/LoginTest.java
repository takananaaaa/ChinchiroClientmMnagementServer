import Control.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
    }

    @Test
    @DisplayName("setLoginInfo: ユーザー名とパスワードが正しく保持されるか")
    void testSetLoginInfo() {
        login.setLoginInfo("UserA", "Pass123");
        assertEquals("UserA", login.getUserName());
        assertEquals("Pass123", login.getPassword());
    }

    @Nested
    @DisplayName("checkInput: 入力値バリデーションのテスト")
    class CheckInputTest {

        @Test
        @DisplayName("正常な入力（両方あり）ならtrue")
        void testCheckInput_Valid() {
            login.setLoginInfo("user", "pass");
            assertTrue(login.checkInput());
        }

        @ParameterizedTest
        @CsvSource({
                ", pass",      // ユーザー名がnull
                "'', pass",    // ユーザー名が空
                "user, ",      // パスワードがnull
                "user, ''",    // パスワードが空
                ", ''"         // 両方不正
        })
        @DisplayName("不正な入力（nullまたは空文字）ならfalse")
        void testCheckInput_Invalid(String name, String pass) {
            login.setLoginInfo(name, pass);
            assertFalse(login.checkInput());
        }
    }

    @Nested
    @DisplayName("login: 認証処理のテスト")
    class LoginExecutionTest {

        @Test
        @DisplayName("入力が不正な場合にIllegalStateExceptionを投げるか")
        void testLogin_ThrowsException() {
            // 何もセットしない状態（null）
            assertThrows(IllegalStateException.class, () -> {
                login.login();
            }, "未入力状態でloginを呼ぶと例外が発生すべき");
        }

        /* 注意: 実際のDB通信(DatabaseServerCommunication)が発生するため、
         このテストを実行するにはDBサーバが起動している必要があります。
         本来は、ClientManagementControllerの時と同様に
         DB通信部分をMockにするのが理想的です。
        */
    }
}