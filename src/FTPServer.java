import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FTPServer implements Runnable {

    private static int SERVER_PORT = 7735;
    private final String FILE_PATH;
    private final PseudoHeader pseudoHeader;

    private String CLIENT_HOSTNAME;
    private int CLIENT_PORT;
    private DatagramSocket socket;
    private InetAddress clientAddress;
    private InetAddress serverAddress;
    private GoBackNReceiverWindow window;



    public FTPServer(String filename) {
        this(SERVER_PORT, filename);
    }

    public FTPServer(int serverPort, String filename) {
        SERVER_PORT = serverPort;
        FILE_PATH = filename;
        pseudoHeader = new PseudoHeader();
    }


    @Override
    public void run() {
        try {
            socket = new DatagramSocket(SERVER_PORT);
            serverAddress = InetAddress.getByName("localhost");

        } catch (SocketException e) {
            System.out.println("Failed to open UDP socket at port " + SERVER_PORT + ": " + e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println("Failed to find the address of localhost: " + e.getMessage());
        }
    }


    private void rdt_receive() {

    }
}
