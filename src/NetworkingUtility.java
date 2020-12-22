import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class NetworkingUtility {
    private final App app;

    private final int PORT = 9192;
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread listeningThread = null;
    private String user = null;

    public void setUser(String user) {
        this.user = user;
    }

    public NetworkingUtility(App app) throws Exception {
        Debug.debug("Creating NetworkingUtility");

        this.app = app;

        Debug.debug("Connecting to Server...");
        socket = new Socket("localhost", PORT);
        Debug.debug("Successfully connected to Server");

        Debug.debug("Connection to the OutputStream...");
        this.out = new ObjectOutputStream(socket.getOutputStream());
        Debug.debug("Successfully connected to the OutputStream");

        Debug.debug("Connecting to the InputStream...");
        this.in = new ObjectInputStream(socket.getInputStream());
        Debug.debug("Successfully connected to the InputStream");

        startListening();
    }

    public void close() {
        try {
//            in.close();
//            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object read() throws IOException, ClassNotFoundException {
//        return in.readObject();
        return in.readUnshared();
    }

    private void write(Object obj) throws IOException {
//        out.writeObject(obj);
        out.writeUnshared(obj);
    }

    private void startListening() {
        listeningThread = new Thread(() -> {
            Thread.currentThread().setName("ListeningThread");
            Debug.debug("Started " + Thread.currentThread().getName());

            while (true) {
                Debug.debug("Waiting for Server's request...");

                try {
//                    String msg = (String) read();
//                    Debug.debug("Server says: " + msg);

                    String request = (String) read();
                    Debug.debug("Got request: " + request);
                    manageRequest(request);
                }
                catch (EOFException e) {
                    e.printStackTrace();
                    Debug.debug("EOF reached");
                    break;
                }
                catch (SocketException e) {
                    e.printStackTrace();
                    Debug.debug("Socket closed");
                    break;
                }
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }

            Debug.debug("Closing " + Thread.currentThread().getName());
        });
        listeningThread.start();
    }

    synchronized private void manageRequest(String request) throws IOException, ClassNotFoundException {
        Debug.debug("Working on request: " + request);

        if (request.equals("login-confirmation")) {
            manageLoginConfirmation();
        }
        else if (request.equals("updated-car-list")) {
            updateCarList();
        }
        else if (request.equals("buy-car-confirmation")) {
            manageBuyCarConfirmation();
        }
        else if (request.equals("add-car-confirmation")) {
            manageAddCarConfirmation();
        }
    }

    private void manageAddCarConfirmation() throws IOException, ClassNotFoundException {
        String resp = (String) read();

        if (resp.equals("failed")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could not add car");
                alert.showAndWait();
            });
        }
        else if (resp.equals("successful")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Successfully added car");
                alert.showAndWait();
            });
        }
    }

    // manages buy car confirmation received from the server
    private void manageBuyCarConfirmation() throws IOException, ClassNotFoundException {
        String res = (String) read();

        if (res.equals("failed")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Could not buy car");
                alert.showAndWait();
            });
        }
        else {
            Car car = (Car) read();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Successfully brought car (Model: " + car.getModel() + ")");
                alert.showAndWait();
            });
            getUpdatedCarListRequest();
        }
    }

    // updates the car list
    private void updateCarList() throws IOException, ClassNotFoundException {
        ArrayList<Car> carList = (ArrayList<Car>) read();
        app.updateCarList(carList);
    }

    // manages login confirmation received from the server
    private void manageLoginConfirmation() throws IOException, ClassNotFoundException {
        String resp = (String) read();
        if (resp.equals("as-viewer")) {
            setUser("viewer"); // set current user as viewer
            getUpdatedCarListRequest(); // get the updated car list from the server

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Logging in as a viewer");
                alert.showAndWait();
                app.getAllScenes().getHomePageController().setUser("viewer");
                app.showHomePage();
            });
        }
        else if (resp.equals("successful")) {
            String username = (String) read();

            setUser(username); // set current user as viewer
            getUpdatedCarListRequest(); // get the updated car list from the server

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Logging in as " + username);
                alert.showAndWait();
                app.getAllScenes().getHomePageController().setUser(username);
                app.showHomePage();
            });
        }
        else if (resp.equals("no-such-user")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("No such user");
                alert.showAndWait();
            });
        }
        else if (resp.equals("failed")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Wrong password");
                alert.showAndWait();
            });
        }
    }

    // sends a login request to the server
    synchronized public void loginRequest(String username, String password) throws IOException {
        Debug.debug("sending login request");
        Debug.debug("username: " + username + ", password: " + password);

        write("login");
        write(username);
        write(password);

        Debug.debug("successfully sent login request");
    }

    // sends a search by registration number request
    synchronized public void searchByRegRequest(String reg) throws IOException {
        write("search-by-reg");
        write(reg);
    }

    // sends a search by make and model request
    synchronized public void searchByMakeAndModelRequest(String make, String model) throws IOException {
        write("search-by-make-and-model");
        write(make);
        write(model);
    }

    // sends a request to the server for the updated car list
    synchronized public void getUpdatedCarListRequest() throws IOException {
        write("get-updated-car-list");
    }

    // sends a buy car request to the server
    synchronized public void buyCarRequest(String reg) throws IOException {
        write("buy-car");
        write(reg);
    }

    // sends a edit car request to the server
    synchronized public void editCarRequest(Car car) throws IOException {
        write("edit-car");
        write(car);
    }

    // sends a add car request to the server
    synchronized public void addCarRequest(Car car) throws IOException {
        write("add-car");
        write(car);
    }

    // sends a delete car request to the server
    synchronized public void deleteCarRequest(String reg) throws IOException {
        write("delete-car");
        write(reg);
    }

}
