import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginPageController {
    @FXML
    private Button resetButton;
    @FXML
    private Button logInButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private App app = null;

    @FXML
    private void initialize() {
        Debug.debug("in initializer of " + getClass().getName());
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void logInButtonClicked(ActionEvent actionEvent) {
        Debug.debug("Login button clicked");

        // check if the username is blank
        if (usernameField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please enter a username");
            alert.showAndWait();
            return;
        }

        Debug.debug("username: " + usernameField.getText() + ", password: " + passwordField.getText());
        // send login-in request
        try {
            app.getNetUtil().loginRequest(usernameField.getText(), passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetButtonClicked(ActionEvent actionEvent) {
        reset();
    }

    public void reset() {
        usernameField.clear();
        passwordField.clear();
    }
}
