package Control;

import Communication.DatabaseServerCommunication;

/*
 ログイン処理に関連するデータ保持と検証を行うクラス
*/
public class Login {

    private String userName;
    private String password;

    // デフォルトコンストラクタ
    public Login() {
    }

    /*
     ログイン情報をセットするためのメソッド
    */
    public void setLoginInfo(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    /*
      入力されたユーザー名とパスワードが有効かチェックする
      @return 有効であれば true
    */
    public boolean checkInput() {
        return userName != null && !userName.isEmpty()
                && password != null && !password.isEmpty();
    }

    /**
     * データベースと通信してログイン認証を行う
     * (NewRegistrationクラスと同様の構成)
     * @return 認証成功ならtrue
     */
    public boolean login() {
        // 入力チェック
        if (!checkInput()) {
            throw new IllegalStateException("UserName or Password is invalid");
        }

        // DBサーバとの通信
        DatabaseServerCommunication dbComm = new DatabaseServerCommunication();
        return dbComm.login(userName, password);
    }
}
