import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.LogManager;

public class StartServer {
    public static void main(String[] args){
        try{
            LogManager logManager = LogManager.getLogManager();
            logManager.readConfiguration(new FileInputStream("logging.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Server();
    }
}
