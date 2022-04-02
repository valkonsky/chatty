public class DBAuthService implements AuthService {

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        return SQLHandler.getNicknameByLoginAndPassword(login,password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SQLHandler.registration(login,password,nickname);
    }

    @Override
    public boolean changeNickname(String currentNick, String newNick) {
        return SQLHandler.changeNickname(newNick,currentNick);
    }
}
