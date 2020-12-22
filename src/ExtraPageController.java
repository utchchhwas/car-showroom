import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ExtraPageController {
    @FXML
    private Label title;
    @FXML
    private Button backBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField regField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField color1Field;
    @FXML
    private TextField color2Field;
    @FXML
    private TextField color3Field;
    @FXML
    private TextField makeField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField priceFiled;
    @FXML
    private TextField quantityField;

    private String type = null;
    private App app = null;

    public void setApp(App app) {
        this.app = app;
    }

    public void setType(String type) {
        this.type = type;
        if (type.equals("edit-car")) {
            title.setText("Edit Car");
            regField.setDisable(true);
        }
        else if (type.equals("add-car")) {
            title.setText("Add Car");
            regField.setDisable(false);
        }
        reset();
    }

    public void backBtnPressed(ActionEvent actionEvent) {
        app.showHomePage();
    }

    public void saveBtnPressed(ActionEvent actionEvent) {
        Car car = null;
        try {
            car = getCar();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please fill the info correctly");
            alert.showAndWait();
            return;
        }

        try {
            if (type.equals("edit-car")) {
                app.getNetUtil().editCarRequest(car);
            }
            else if (type.equals("add-car")) {
                app.getNetUtil().addCarRequest(car);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        app.showHomePage();
    }

    private Car getCar() throws Exception {
        String reg = regField.getText();
        int year = Integer.parseInt(yearField.getText());
        String color1 = color1Field.getText();
        String color2 = color2Field.getText();
        String color3 = color3Field.getText();
        String make = makeField.getText();
        String model = modelField.getText();
        int price = Integer.parseInt(priceFiled.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        if (reg.equals("") || make.equals("") || model.equals("")) {
            throw new Exception();
        }

        return new Car(reg, year, color1, color2, color3, make, model, price, quantity);
    }

    public void setCar(Car car) {
        regField.setText(car.getReg());
        yearField.setText(Integer.toString(car.getYear()));
        color1Field.setText(car.getColor1());
        color2Field.setText(car.getColor2());
        color3Field.setText(car.getColor3());
        makeField.setText(car.getMake());
        modelField.setText(car.getModel());
        priceFiled.setText(Integer.toString(car.getPrice()));
        quantityField.setText(Integer.toString(car.getQuantity()));
    }

    public void reset() {
        regField.clear();
        yearField.clear();
        color1Field.clear();
        color2Field.clear();
        color3Field.clear();
        makeField.clear();
        modelField.clear();
        priceFiled.clear();
        quantityField.clear();
    }

}
