import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientHandler {
    int id;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    Database db;
    String clientUsername = null;
    Thread listeningThread;

    public ClientHandler(Socket socket, int id) throws IOException {
        Debug.debug("Creating ClientHandler for Server#" + id);

        this.socket = socket;
        this.id = id;

        Debug.debug("Connecting to the InputStream...");
        this.in = new ObjectInputStream(socket.getInputStream());
        Debug.debug("Successfully connected to the InputStream");

        Debug.debug("Connection to the OutputStream...");
        this.out = new ObjectOutputStream(socket.getOutputStream());
        Debug.debug("Successfully connected to the OutputStream");

        this.db = Database.getDatabase();

        startListening();
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
            Thread.currentThread().setName("ListeningThread#" + id);
            Debug.debug("Started " + Thread.currentThread().getName());

            while (true) {
                Debug.debug("Waiting for Client's request...");

                try {
//                    String msg = (String) read();
//                    Debug.debug("Client says: " + msg);

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
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }


            Debug.debug("Closing " + Thread.currentThread().getName());
        });
        listeningThread.start();
    }

    private void manageRequest(String request) throws IOException, ClassNotFoundException, SQLException {
        Debug.debug("Working on request: " + request);

        if (request.equals("login")) {
            manageLogin();
        }
        else if (request.equals("search-by-reg")) {
            manageSearchByReg();
        }
        else if (request.equals("search-by-make-and-model")) {
            manageSearchByMakeAndModel();
        }
        else if (request.equals("get-updated-car-list")) {
            manageGetUpdatedCarListRequest();
        }
        else if (request.equals("buy-car")) {
            manageBuyCarRequest();
        }
        else if (request.equals("edit-car")) {
            manageEditCarRequest();
        }
        else if (request.equals("add-car")) {
            manageAddCarRequest();
        }
        else if (request.equals("delete-car")) {
            manageDeleteCarRequest();
        }
    }

    private void manageDeleteCarRequest() throws IOException, ClassNotFoundException, SQLException {
        String reg = (String) read();

        db.deleteCar(reg);

        sendCarList(db.getAllCars());

        sendUpdateCalListToAllClients();
    }

    private void manageAddCarRequest() throws IOException, ClassNotFoundException, SQLException {
        Car car = (Car) read();

        write("add-car-confirmation");
        try {
            db.addCar(car);
        }
        catch (SQLException e) {
            Debug.debug("Could not add car");
            write("failed");
            sendCarList(db.getAllCars());
            return;
        }
        write("successful");
        sendCarList(db.getAllCars());

        sendUpdateCalListToAllClients();
    }

    private void manageEditCarRequest() throws IOException, ClassNotFoundException, SQLException {
        Car car = (Car) read();

        db.deleteCar(car.getReg());
        db.addCar(car);

        sendCarList(db.getAllCars());

        sendUpdateCalListToAllClients();
    }

    private void manageBuyCarRequest() throws IOException, ClassNotFoundException, SQLException {
        String reg = (String) read();

        boolean flag = db.buyCar(reg);

        write("buy-car-confirmation");
        if (flag) {
            write("successful");
            write(db.getCar(reg));

            sendUpdateCalListToAllClients();
        }
        else {
            write("failed");
        }
    }

    private void manageGetUpdatedCarListRequest() throws SQLException, IOException {
        ArrayList<Car> carList = db.getAllCars();

        sendCarList(carList);
    }

    private void sendCarList(ArrayList<Car> carList) throws IOException {
        write("updated-car-list");
        write(carList);
    }

    private void manageSearchByReg() throws IOException, ClassNotFoundException, SQLException {
        Debug.debug("handling search by reg request...");

        String reg = (String) read();
        Debug.debug("reg: " + reg);

        if (reg.equals("")) {
            sendCarList(db.getAllCars());
            return;
        }

        Car car = db.getCar(reg);
        ArrayList<Car> carList = new ArrayList<>();
        if (car != null) carList.add(car);
        sendCarList(carList);

        Debug.debug("handled search by reg request");
    }

    private void manageSearchByMakeAndModel() throws IOException, ClassNotFoundException, SQLException {
        Debug.debug("handling search by make and model request");

        String make = (String) read();
        String model = (String) read();

        ArrayList<Car> carList = db.getCar(make, model);
        sendCarList(carList);

        Debug.debug("handled search by make and model request");
    }

    private void manageLogin() throws IOException, ClassNotFoundException, SQLException {
        Debug.debug("handling login request...");

        String username = (String) read();
        String password = (String) read();
        Debug.debug("username: " + username + ", password: " + password);

        String realPassword = db.getPassword(username);
        Debug.debug("real password: " + realPassword);

        write("login-confirmation");
        if (username.equals("viewer")) {
            write("as-viewer");
            clientUsername = username;
        }
        else if (realPassword == null) {
            write("no-such-user");
        }
        else if (password.equals(realPassword)) {
            write("successful");
            write(username);
            clientUsername = username;
        }
        else {
            write("failed");
        }

        if (clientUsername != null) {
            listeningThread.setName("ListeningThread#" + id + "<" + clientUsername + ">");
        }

        Debug.debug("Handled login request");
    }

    private void sendUpdateCalListToAllClients() throws SQLException, IOException {
        ArrayList<Server> allServers = Server.getAllServers();
        ArrayList<Car> carList = db.getAllCars();
        for (Server server : allServers) {
            server.getClientHandler().sendCarList(carList);
        }
    }
}
