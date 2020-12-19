import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        if (request.equals("get-updated-car-list")) {
            manageGetUpdatedCarListRequest();
        }
        else if (request.equals("buy-car")) {
            manageBuyCarRequest();
        }

    }

    private void manageBuyCarRequest() throws IOException, ClassNotFoundException, SQLException {
        String reg = (String) read();

        boolean flag = db.buyCar(reg);

        write("buy-car-confirmation");
        if (flag) {
            write("successful");
            write(db.getCar(reg));
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

    private void manageSearchByReg() throws IOException, ClassNotFoundException {
        Debug.debug("In manageSearchByReg()");

        String reg = (String) read();

        Debug.debug("reg: " + reg);

        Debug.debug("Querying database...");
        Car car = null;
        try {
            car = db.getCar(reg);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Debug.debug("Found: " + car);

        ArrayList<Car> carList = new ArrayList<>();
        if (car != null) carList.add(car);
        sendCarList(carList);
    }

    private void manageLogin() throws IOException, ClassNotFoundException, SQLException {
        Debug.debug("In manageLogin()");

        String username = (String) read();
        String password = (String) read();

        Debug.debug("username: " + username + ", password: " + password);

        String actualPaasword = db.getPassword(username);
        Debug.debug("actual password: " + actualPaasword);

        write("login-confirmation");
        if (username.equals("viewer")) {
            write("as-viewer");
            clientUsername = username;
        }
        else if (actualPaasword == null) {
            write("no-such-user");
        }
        else if (password.equals(actualPaasword)) {
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
    }
}
