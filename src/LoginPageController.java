import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginPageController {
    public Button resetButton;
    public Button logInButton;
    public TextField usernameField;
    public PasswordField passwordField;

    private App app = null;

    @FXML
    private void initialize() {
        Debug.debug("In initializer of " + getClass().getName());
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void logInButtonClicked(ActionEvent actionEvent) {
        Debug.debug("Log In button clicked");

        if (usernameField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please provide a username");
            alert.showAndWait();
            return;
        }

        try {
            app.getNetUtil().sendLoginRequest(usernameField.getText(), passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void resetButtonClicked(ActionEvent actionEvent) {
        Debug.debug("Reset button clicked");

        usernameField.clear();
        passwordField.clear();
    }
}
