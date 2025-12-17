package Control;

/*
 ログイン処理に関連するデータ保持と検証を行うクラス
*/
public class Login {

    // クラス図には明記されていないが、データを保持するためにフィールドが必要
    private String userName;
    private String password;

    // デフォルトコンストラクタ
    public Login() {
    }

    /*
     ログイン情報をセットするためのメソッド
     (クラス図にはありませんが、これがないとデータが入らないため追加）
    */
    public void setLoginInfo(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /*
     ユーザー名を取得する
     クラス図では void だが、実用上 String を返すように実装する。
     @return userName
    */
    public String getUserName() {
        return this.userName;
    }

    /*
     パスワードを取得する
     クラス図では void だが、実用上 String を返すように実装する。
     @return password
    */
    public String getPassword() {
        return this.password;
    }

    /*
      入力されたユーザー名とパスワードが有効かチェックする
      @return 有効であれば true
    */
    public boolean checkInput() {
        // null または 空文字でないことを確認
        return userName != null && !userName.isEmpty()
                && password != null && !password.isEmpty();
    }
}
