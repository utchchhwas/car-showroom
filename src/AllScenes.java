

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class AllScenes {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final App app;

    Scene loginPage = null;
    LoginPageController loginPageController = null;

    Scene homePage = null;
    HomePageController homePageController = null;

    Scene extraPage = null;
    ExtraPageController extraPageController = null;

    public AllScenes(App app) {
        this.app = app;
    }

    private void loadLoginPage() {
        Debug.debug("Loading login page...");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(app.getClass().getResource("login-page.fxml"));
        try {
            Parent root = loader.load();
            loginPage = new Scene(root, 600, 400);
            loginPageController = loader.getController();
            loginPageController.setApp(app);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Debug.debug("Successfully loaded login page");
    }

    public Scene getLoginPage() {
        if (loginPage == null) {
            loadLoginPage();
        }
        return loginPage;
    }

    public LoginPageController getLoginPageController() {
        if (loginPageController == null) {
            loadLoginPage();
        }
        return loginPageController;
    }

    private void loadHomePage() {
        Debug.debug("Loading home page...");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(app.getClass().getResource("home-page.fxml"));
        try {
            Parent root = loader.load();
            homePage = new Scene(root, 800, 600);
            homePageController = loader.getController();
            homePageController.setApp(app);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Debug.debug("Successfully loaded home page");
    }

    public Scene getHomePage() {
        if (homePage == null) {
            loadHomePage();
        }
        return homePage;
    }

    public HomePageController getHomePageController() {
        if (homePageController == null) {
            loadHomePage();
        }
        return homePageController;
    }

    private void loadExtraPage() {
        Debug.debug("Loading extra page...");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(app.getClass().getResource("extra-page.fxml"));
        try {
            Parent root = loader.load();
            extraPage = new Scene(root, 800, 600);
            extraPageController = loader.getController();
            extraPageController.setApp(app);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Debug.debug("Successfully loaded extra page");
    }

    public Scene getExtraPage() {
        if (extraPage == null) {
            loadExtraPage();
        }
        return extraPage;
    }

    public ExtraPageController getEditCarPageController() {
        if (extraPageController == null) {
            loadExtraPage();
        }
        return extraPageController;
    }

}
