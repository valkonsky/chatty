import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService{

    private class UserData{
        String login;
        String password;
        String nickname;

        UserData(String login,String password,String nickname){
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<UserData> users;

    SimpleAuthService(){
        users = new ArrayList<>();
        users.add(new UserData("qwerty","qwerty","qwerty"));
        users.add(new UserData("nick","qwerty","nick"));
        users.add(new UserData("userPro","qwerty","user1"));

        for (int i = 0; i < 9; i++) {
            users.add(new UserData("user" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login)&& user.password.equals(password)){
                return user.nickname;
            }
        }
        return null;
    }


}
