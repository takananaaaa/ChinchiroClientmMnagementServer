package Control;

import Communication.DatabaseServerCommunication;

public class NewRegistration {

    private String userName;
    private String password;

    public NewRegistration(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean checkUserID(){
        if(userName == null) return false;
        if(userName.isEmpty()) return false;
        return true;
    }

    public boolean checkPassword() {
        if (password == null) return false;
        if (password.isEmpty()) return false;

        return true;
    }



    /*
    public void registerToDatabase() {
        // 入力チェック
        if (!checkUserID() || !checkPassword()) {
            throw new IllegalStateException("UserID or Password is invalid");
        }



        // DBサーバとの通信
        DatabaseServerCommunication dbComm = new DatabaseServerCommunication();
        dbComm.processQuery(userID, password);
    }
*/

}
