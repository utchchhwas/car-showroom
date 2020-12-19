import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class NetworkingUtility {
    private final App app;

    private final int PORT = 9192;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Thread listeningThread = null;
    private String user = null;


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
    }

    private void manageBuyCarConfirmation() throws IOException, ClassNotFoundException {
        String res = (String) read();

        if (res.equals("failed")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Buy car request failed");
                alert.showAndWait();
            });
        }
        else {
            Car car = (Car) read();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Successfully brought car (Model: " + car.getModel() + ")");
                alert.showAndWait();
            });
            getUpdatedCarListRequest();
        }
    }

    private void updateCarList() throws IOException, ClassNotFoundException {
        ArrayList<Car> carList = (ArrayList<Car>) read();
        app.updateCarList(carList);
    }


    private void manageUpdateCarList() throws IOException, ClassNotFoundException {
        ArrayList<Car> carList = (ArrayList<Car>) read();

        app.updateCarList(carList);
    }

    synchronized private void manageLoginConfirmation() throws IOException, ClassNotFoundException {
        String resp = (String) read();
        if (resp.equals("as-viewer")) {
            Debug.debug("Logging in as viewer");

            setUser("viewer");

//            Debug.debug("Getting all cars...");
//            ArrayList<Car> cars = (ArrayList<Car>) read();
//            Debug.debug("Succesfully got all cars");

            getUpdatedCarListRequest();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Logging in as a viewer");
                alert.showAndWait();
                app.showViewerHomePage();
            });

//            app.updateCarList(cars);
        }
        else if (resp.equals("successful")) {
            String username = (String) read();
            Debug.debug("Logging in as user: " + username);

            setUser(username);

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Logging in as " + username);
                alert.showAndWait();
            });
        }
        else if (resp.equals("no-such-user")) {
            Debug.debug("No such user");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No such user");
                alert.showAndWait();
            });
        }
        else if (resp.equals("failed")) {
            Debug.debug("Wrong password");

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong password");
                alert.showAndWait();
            });
        }
    }

    public void setUser(String user) {
        this.user = user;
    }

    synchronized public void sendLoginRequest(String username, String password) throws IOException {
        Debug.debug("Sending login request");

        Debug.debug("username: " + username + ", password: " + password);

        write("login");
        write(username);
        write(password);

        Debug.debug("Successfully sent login request");
    }

    synchronized public void sendSearchByRegRequest(String reg) throws IOException {
        Debug.debug("Sending search by reg request");

        Debug.debug("reg: " + reg);

        write("search-by-reg");
        write(reg);

        Debug.debug("Successfully sent search by reg request");
    }

    synchronized public void getUpdatedCarListRequest() throws IOException {
        write("get-updated-car-list");
    }

    synchronized public void buyCarRequest(String reg) throws IOException {
        write("buy-car");
        write(reg);
    }

}
