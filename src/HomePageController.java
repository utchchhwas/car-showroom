import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomePageController {
    @FXML
    private Button buyBtn;
    @FXML
    private Button searchByMakeAndModelBtn;
    @FXML
    private Button searchByRegBtn;
    @FXML
    private TextField modelField;
    @FXML
    private TextField makeField;
    @FXML
    private TextField regField;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button editCarBtn;
    @FXML
    private Button addCarBtn;
    @FXML
    private Button deleteCarBtn;
    @FXML
    private Label userLabel;
    @FXML
    private VBox viewerOptions;
    @FXML
    private HBox manufacturerOptions;

    @FXML
    private TableView<Car> tableView;
    @FXML
    private TableColumn<Car, String> regCol;
    @FXML
    private TableColumn<Car, Integer> yearCol;
    @FXML
    private TableColumn<Car, String> colorsCol;
    @FXML
    private TableColumn<Car, String> makeCol;
    @FXML
    private TableColumn<Car, String> modelCol;
    @FXML
    private TableColumn<Car, Integer> priceCol;
    @FXML
    private TableColumn<Car, Integer> quantityCol;

    private App app = null;

    @FXML
    private void initialize() {
        Debug.debug("in initializer of " + getClass().getName());

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

    // sets the current user
    public void setUser(String user) {
        if (user.equals("viewer")) {
            viewerOptions.setDisable(false);
            manufacturerOptions.setDisable(true);
            userLabel.setText("Logged in as a viewer");
        }
        else {
            viewerOptions.setDisable(true);
            manufacturerOptions.setDisable(false);
            userLabel.setText("Logged in as " + user);
        }
    }

    public void logoutBtnPressed(ActionEvent actionEvent) {
        reset();
        app.getNetUtil().setUser(null); // set current user to null
        app.showLoginPage();
    }

    public void refreshBtnPressed(ActionEvent actionEvent) {
        try {
            app.getNetUtil().getUpdatedCarListRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchByRegBtnPressed(ActionEvent actionEvent) {
        String reg = regField.getText();

        try {
            app.getNetUtil().searchByRegRequest(reg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchByMakeAndModelBtnPressed(ActionEvent actionEvent) {
        String make = makeField.getText();
        String model = modelField.getText();

        if (make.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please enter a car make");
            alert.showAndWait();
            return;
        }

        try {
            app.getNetUtil().searchByMakeAndModelRequest(make, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buyBtnPressed(ActionEvent actionEvent) {
        Car car = tableView.getSelectionModel().getSelectedItem();
        if (car == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please select a car from the table");
            alert.showAndWait();
            return;
        }

        String reg = car.getReg();

        try {
            app.getNetUtil().buyCarRequest(reg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editCarBtnPressed(ActionEvent actionEvent) {
        Car car = tableView.getSelectionModel().getSelectedItem();
        if (car == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please select a car from the table");
            alert.showAndWait();
            return;
        }

        app.getAllScenes().getEditCarPageController().setType("edit-car");
        app.getAllScenes().getEditCarPageController().setCar(car);
        app.showExtraPage();
    }

    public void addCarBtnPressed(ActionEvent actionEvent) {
        app.getAllScenes().getEditCarPageController().setType("add-car");
        app.showExtraPage();
    }

    public void deleteCarBtnPressed(ActionEvent actionEvent) {
        Car car = tableView.getSelectionModel().getSelectedItem();
        if (car == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please select a car from the table");
            alert.showAndWait();
            return;
        }

        try {
            app.getNetUtil().deleteCarRequest(car.getReg());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        regField.clear();
        makeField.clear();
        modelField.clear();
    }
}
