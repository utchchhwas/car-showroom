import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class App extends Application {
    public AllScenes getAllScenes() {
        return allScenes;
    }

    private AllScenes allScenes = null;
    private Stage primaryStage = null;
    private NetworkingUtility netUtil = null;

    public NetworkingUtility getNetUtil() {
        return netUtil;
    }

    private final ObservableList<Car> carList = FXCollections.observableArrayList();

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
    public void start(Stage primaryStage) throws IOException {
        Debug.debug("Enterted start()");

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

        Debug.debug("Exiting start()");
    }

    void showLoginPage() {
        primaryStage.setTitle("Log In");
        primaryStage.setScene(allScenes.getLoginPage());
        primaryStage.show();
    }

    void showViewerHomePage() {
        primaryStage.setTitle("Viewer Home Page");
        primaryStage.setScene(allScenes.getViewerHomePage());
        primaryStage.show();
    }
}
