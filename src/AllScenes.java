

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

    Scene viewerHomePage = null;
    ViewerHomePageController viewerHomePageController = null;

    public AllScenes(App app) {
        this.app = app;
    }

    private void loadLoginPage() {
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

    private void loadViewerHomePage() {
        Debug.debug("Loading viewer home page...");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(app.getClass().getResource("viewer-home-page.fxml"));
        try {
            Parent root = loader.load();
            viewerHomePage = new Scene(root, 800, 600);
            viewerHomePageController = loader.getController();
            viewerHomePageController.setApp(app);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Debug.debug("Successfully loaded viewer home page");
    }

    public Scene getViewerHomePage() {
        if (viewerHomePage == null) {
            loadViewerHomePage();
        }
        return viewerHomePage;
    }

    public ViewerHomePageController getViewerHomePageController() {
        if (viewerHomePageController == null) {
            loadViewerHomePage();
        }
        return viewerHomePageController;
    }

}
