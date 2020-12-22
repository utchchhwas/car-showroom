import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {
    private final static int PORT = 9192;
    private static final ArrayList<Server> allServers = new ArrayList<>();

    private final int serverId;
    private final ClientHandler clientHandler;

    public int getServerId() {
        return serverId;
    }

    Server(Socket socket, int serverId) throws IOException {
        Debug.debug("Creating Server#" + serverId + "...");
        Debug.debug("Server#" + serverId + " is connected to " + socket.getRemoteSocketAddress());

        this.serverId = serverId;
        this.clientHandler = new ClientHandler(socket, serverId);

        Debug.debug("Successfully created Server#" + serverId);
    }


    public static void main(String[] args) {
        Thread.currentThread().setName("MainServerThread");
        Debug.debug("Server started");

        Debug.debug("Creating ServerSocket...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
            Debug.debug("Failed to create ServerSocket");
            System.exit(1);
        }
        Debug.debug("Successfully created ServerSocket");


        for (int i = 1; ; i++) {
            Debug.debug("Waiting for connection #" + i);

            Socket socket = null;
            try {
                socket = serverSocket.accept();
                Debug.debug("Successfully established connection #" + i);
                try {
                    allServers.add(new Server(socket, i));
                } catch (IOException e) {
                    e.printStackTrace();
                    Debug.debug("Failed to create Server#" + i);
                }
            }
            catch (SocketException e) {
                Debug.debug("ServerSocket closed");
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
                Debug.debug("Failed to establish connection #" + i);
            }
        }

        Debug.debug("Server closing");
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public static ArrayList<Server> getAllServers() {
        return allServers;
    }
}
