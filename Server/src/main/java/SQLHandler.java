import java.sql.*;

public class SQLHandler {

    private static Connection connection;
    private static PreparedStatement psGetNickByLoginAndPass;
    private static PreparedStatement psChangeNick;
    private static PreparedStatement psReg;

    public static String getNicknameByLoginAndPassword(String login, String password) {
        String nickname = null;
        try {
            psGetNickByLoginAndPass.setString(1, login);
            psGetNickByLoginAndPass.setString(2, password);
            ResultSet resultSet = psGetNickByLoginAndPass.executeQuery();
            if (resultSet.next())
            nickname  = resultSet.getString("nickname");
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nickname;
    }

    public static boolean changeNickname(String newNick,String currentNick){
        try {
            psChangeNick.setString(1,newNick);
            psChangeNick.setString(2,currentNick);
            psChangeNick.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean registration(String login, String password, String nickname) {
        try {
            psReg.setString(1,login);
            psReg.setString(2,password);
            psReg.setString(3,nickname);
            psReg.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chattybase.db");
            preparedALlStatements();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void disconnect(){
        try {
            psGetNickByLoginAndPass.close();
            psChangeNick.close();
            psReg.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void preparedALlStatements() throws Exception {
        psGetNickByLoginAndPass = connection.prepareStatement("SELECT * FROM 'users' WHERE login = ? AND password = ?");
        psChangeNick = connection.prepareStatement("UPDATE 'users' SET nickname = ? WHERE nickname = ?");
        psReg = connection.prepareStatement("INSERT INTO users (login,password,nickname) VALUES (?,?,?)");
    }
}
