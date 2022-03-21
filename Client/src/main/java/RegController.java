import constants.Command;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nicknameField;
    @FXML
    public TextArea textArea;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private Controller controller;

    @FXML
    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nickname = nicknameField.getText().trim();

        controller.registration(login,password,nickname);
    }

    public void result(String command){
        if (command.equals(Command.REG_OK)){
            textArea.appendText("Регистрация прошла успешно\n");
        }else{
            textArea.appendText("Логин или никнейм заняты");
        }
    }
}
