import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Client {


    public static void main(String[] args) throws IOException {
        Debug.debug("Connecting to the server...");
        Socket socket = new Socket("localhost", 9192);
        Debug.debug("Successfully connected to the server");

        Debug.debug("Connecting to the OutputStream...");
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        Debug.debug("Successfully connected to the OutputStream");

        Debug.debug("Connecting to the InputStream...");
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Debug.debug("Successfully connected to the OutputStream");

        new Thread(() -> {
            Thread.currentThread().setName("ListeningThread");

            while (true) {
                Debug.debug("Waiting for Server's response...");
                try {
                    String msg = (String) in.readUnshared();
                    Debug.debug("Server says: " + msg);

//                    if (msg.equals("sending all cars")) {
//                        ArrayList<Car> cars = (ArrayList<Car>) in.readUnshared();
//                        for (var car : cars) {
//                            car.printInfo();
//                        }
//                    }
                }
                catch (SocketException e) {
                    Debug.debug("Socket closed");
                    break;
                }
                catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }


        }).start();




        out.writeUnshared("login");
        out.writeUnshared("viewer");
        out.writeUnshared("ANY");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        out.writeUnshared("hey there");

//        for (int i = 0; i < 10; i++) {
//            Debug.debug("Sending msg#" + i + " to Server");
//            String msg = "Hi Server, this is msg#" + i;
//            out.writeUnshared(msg);
//            Debug.debug("Successfully send msg#" + i + " to Server");
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }



        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Debug.debug("Closing Socket");
        socket.close();;
    }
}
