import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class ACKReceiveThread implements Runnable {

    private final DatagramSocket socket;
    private final GoBackNSenderWindow window;
    private final int MSS;
    private AtomicBoolean running;

    public ACKReceiveThread(DatagramSocket socket, GoBackNSenderWindow window, int MSS) {
        this.socket = socket;
        this.window = window;
        this.MSS = MSS;
    }

    @Override
    public void run() {
        byte[] buf = new byte[MSS];
        DatagramPacket packet = new DatagramPacket(buf, MSS);
        running.set(true);
        while (running.get()) {
            try {
                socket.receive(packet);
                byte[] data = packet.getData();
                Segment segment = new Segment(data);
                short type = segment.getType();
                if (type == Segment.ackType) {
                    int seq = segment.getSeqNum();
                    window.receiveACK(seq);
                }
            } catch (IOException e) {
                if (!running.get()) {
                    break;
                }
                System.out.println("Failed to receive packet from server: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running.set(false);
        socket.close();
    }
}
