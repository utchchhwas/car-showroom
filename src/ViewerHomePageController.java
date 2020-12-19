import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;

public class ViewerHomePageController {
    public Button buyBtn;
    public Button searchByMakeAndModelBtn;
    public Button searchByRegBtn;
    public TextField modelField;
    public TextField makeField;
    public TextField regField;
    public Button logoutBtn;
    public Button refreshBtn;

    private App app = null;

    public TableColumn<Car, String> regCol;
    public TableColumn<Car, Integer> yearCol;
    public TableColumn<Car, String> colorsCol;
    public TableColumn<Car, String> makeCol;
    public TableColumn<Car, String> modelCol;
    public TableColumn<Car, Integer> priceCol;
    public TableColumn<Car, Integer> quantityCol;
    public TableView<Car> tableView;

    @FXML
    private void initialize() {
        Debug.debug("In initializer of " + getClass().getName());

        regCol.setCellValueFactory(new PropertyValueFactory<>("reg"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        colorsCol.setCellValueFactory(new PropertyValueFactory<>("colors"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    public void setApp(App app) {
        this.app = app;
        tableView.setItems(app.getCarList());
    }

    public void logoutBtnPressed(ActionEvent actionEvent) {
        Debug.debug("Logout button pressed");

        app.getNetUtil().setUser(null);
        app.showLoginPage();
    }

    public void searchByRegBtnPressed(ActionEvent actionEvent) {
        Debug.debug("Search by registration button pressed");

        String reg = regField.getText();
        Debug.debug("reg: " + reg);

        try {
            app.getNetUtil().sendSearchByRegRequest(reg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshBtnPressed(ActionEvent actionEvent) {
        Debug.debug("Refreshed button pressed");

        try {
            app.getNetUtil().getUpdatedCarListRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buyBtnPressed(ActionEvent actionEvent) {
        Debug.debug("Buy button pressed");

        Car car = tableView.getSelectionModel().getSelectedItem();
        if (car == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a car from the table");
            alert.showAndWait();
            return;
        }
        String reg = car.getReg();
        Debug.debug("selected reg: " + reg);

        try {
            app.getNetUtil().buyCarRequest(reg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
