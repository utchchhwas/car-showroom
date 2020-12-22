import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;

public class App extends Application {
    private Stage primaryStage = null;
    private AllScenes allScenes = null;
    private NetworkingUtility netUtil = null;
    private final ObservableList<Car> carList = FXCollections.observableArrayList();

    public AllScenes getAllScenes() {
        return allScenes;
    }

    public NetworkingUtility getNetUtil() {
        return netUtil;
    }

    public ObservableList<Car> getCarList() {
        return carList;
    }

    public void updateCarList(ArrayList<Car> cars) {
        Debug.debug("Updating car list");
        carList.clear();
        carList.addAll(cars);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        netUtil.close();
    }

    @Override
    public void start(Stage primaryStage) {
//        Debug.debug("Entered start()");

        this.allScenes = new AllScenes(this);
        this.primaryStage = primaryStage;

        showLoginPage();

        try {
            this.netUtil = new NetworkingUtility(this);
        } catch (Exception e) {
            e.printStackTrace();
            Debug.debug("Failed to connect to Server");
            System.exit(1);
        }

//        Debug.debug("Exiting start()");
    }

    void showLoginPage() {
        primaryStage.setTitle("Log In");
        allScenes.getLoginPageController().reset();
        primaryStage.setScene(allScenes.getLoginPage());
        primaryStage.show();
    }

    void showHomePage() {
        primaryStage.setTitle("Home Page");
        primaryStage.setScene(allScenes.getHomePage());
        primaryStage.show();
    }

    void showExtraPage() {
        primaryStage.setTitle("Edit Car Page");
        primaryStage.setScene(allScenes.getExtraPage());
        primaryStage.show();
    }
}
