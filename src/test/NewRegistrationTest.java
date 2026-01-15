package test;

// テスト対象のクラスをインポート
import Control.NewRegistration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NewRegistrationTest {

    @Test
    @DisplayName("コンストラクタで名前とパスワードが正しく設定されるか")
    void testConstructorAndGetters() {
        String name = "TestUser";
        String pass = "password123";

        NewRegistration nr = new NewRegistration(name, pass);

        assertEquals(name, nr.getUserName());
        assertEquals(pass, nr.getPassword());
    }

    @Test
    @DisplayName("checkUserName: 正しい名前ならtrue")
    void testCheckUserName_Valid() {
        NewRegistration nr = new NewRegistration("ValidUser", "pass");
        assertTrue(nr.checkUserName());
    }

    @Test
    @DisplayName("checkUserName: 名前がnullまたは空ならfalse")
    void testCheckUserName_Invalid() {
        // nullの場合
        NewRegistration nrNull = new NewRegistration(null, "pass");
        assertFalse(nrNull.checkUserName(), "nullの場合はfalseになるべき");

        // 空文字の場合
        NewRegistration nrEmpty = new NewRegistration("", "pass");
        assertFalse(nrEmpty.checkUserName(), "空文字の場合はfalseになるべき");
    }

    @Test
    @DisplayName("checkPassword: 正しいパスワードならtrue")
    void testCheckPassword_Valid() {
        NewRegistration nr = new NewRegistration("user", "ValidPass");
        assertTrue(nr.checkPassword());
    }

    @Test
    @DisplayName("checkPassword: パスワードがnullまたは空ならfalse")
    void testCheckPassword_Invalid() {
        // nullの場合
        NewRegistration nrNull = new NewRegistration("user", null);
        assertFalse(nrNull.checkPassword(), "nullの場合はfalseになるべき");

        // 空文字の場合
        NewRegistration nrEmpty = new NewRegistration("user", "");
        assertFalse(nrEmpty.checkPassword(), "空文字の場合はfalseになるべき");
    }

    @Test
    @DisplayName("registerToDatabase: 入力が不正な場合に例外(IllegalStateException)を投げるか")
    void testRegisterToDatabase_ThrowsException() {
        // 名前が空なので不正
        NewRegistration nr = new NewRegistration("", "pass");

        // 実行して例外が出ることを確認
        assertThrows(IllegalStateException.class, () -> {
            nr.registerToDatabase();
        }, "入力チェックに引っかかったら例外を投げるべき");
    }


}