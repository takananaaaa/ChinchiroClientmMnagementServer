package Control;

//import Communication.DatabaseServerCommunication;

import Communication.DatabaseServerCommunication;

public class NewRegistration {

    private String userID;
    private String password;
    private String name;

    public NewRegistration(String userID, String password) {
        this.userID = userID;
        this.password = password;
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public boolean checkUserID(){
        if(userID == null) return false;
        if(userID.isEmpty()) return false;
        return true;
    }

    public boolean checkPassword() {
        if (password == null) return false;
        if (password.isEmpty()) return false;

        return true;
    }




    public boolean registerToDatabase() {
        // 入力チェック
        if (!checkUserID() || !checkPassword()) {
            throw new IllegalStateException("UserID or Password is invalid");
        }



        // DBサーバとの通信
        DatabaseServerCommunication dbComm = new DatabaseServerCommunication();
        return dbComm.registerUser(userID, name, password);
    }


}
