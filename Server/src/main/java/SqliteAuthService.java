import java.sql.*;

public class SqliteAuthService implements AuthService{

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    SqliteAuthService(){
        try {
            connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM 'users' WHERE login = ? AND password = ?");
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            return resultSet.getString("nickname");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM 'users' WHERE login = ?");
            preparedStatement.setString(1,login);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return false;
            else{
                try {
                    preparedStatement = connection.prepareStatement("INSERT INTO 'users' ('login','password','nickname') VALUES(?,?,?)");
                    connection.setAutoCommit(false);
                    preparedStatement.setString(1,login);
                    preparedStatement.setString(2,password);
                    preparedStatement.setString(3,nickname);
                    preparedStatement.executeUpdate();
                    connection.commit();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:chattybase.db");
    }

    public void disconnect(){
        try {
            if (preparedStatement!=null){
                preparedStatement.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
