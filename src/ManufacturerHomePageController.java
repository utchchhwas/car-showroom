import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class ManufacturerHomePageController {
    private App app;

    public Button logoutBtn;
    public Button addCarBtn;
    public Button deleteCarBtn;
    public Button refreshBtn;
    public Button editCarBtn;

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

    public void addCarBtnPressed(ActionEvent actionEvent) {
    }

    public void deleteCarBtnPressed(ActionEvent actionEvent) {
    }

    public void refreshBtnPressed(ActionEvent actionEvent) {
        Debug.debug("Refreshed button pressed");

        try {
            app.getNetUtil().getUpdatedCarListRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editCarBtnPressed(ActionEvent actionEvent) {

    }
}
