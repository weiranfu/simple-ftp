import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

public class ACKReceiveThread implements Runnable {

    private final DatagramSocket socket;
    private final GoBackNSenderWindow window;
    private final int MSS;
    private final List<Timer> timers;
    private boolean running;

    public ACKReceiveThread(DatagramSocket socket, GoBackNSenderWindow window, int MSS, List<Timer> timers) {
        this.socket = socket;
        this.window = window;
        this.MSS = MSS;
        this.timers = timers;
    }

    @Override
    public void run() {
        byte[] buf = new byte[MSS];
        DatagramPacket packet = new DatagramPacket(buf, MSS);
        running = true;
        while (running) {
            try {
                socket.receive(packet);
                byte[] data = new byte[packet.getLength()];    // for storing data.
                System.arraycopy(buf, 0, data, 0, packet.getLength());
                Segment segment = new Segment(data);
                short type = segment.getType();
                if (type == Segment.ackType) {
                    int seq = segment.getSeqNum();
                    window.receiveACK(seq);
                    timers.get(seq).cancel();  // Cancel timer.
                }
            } catch (IOException e) {
                if (!running) {
                    break;
                }
                System.out.println("Failed to receive packet from server: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        socket.close();
        System.out.println("Receiving ACK Thread is closed.");
    }
}
