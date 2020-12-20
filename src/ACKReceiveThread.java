import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.concurrent.Future;

public class ACKReceiveThread implements Runnable {

    private final DatagramSocket socket;
    private final GoBackNSenderWindow window;
    private final int MSS;
    private final Map<Integer, Future<?>> tasksMap;
    private boolean running;

    public ACKReceiveThread(DatagramSocket socket, GoBackNSenderWindow window, int MSS, Map<Integer, Future<?>> tasksMap) {
        this.socket = socket;
        this.window = window;
        this.MSS = MSS;
        this.tasksMap = tasksMap;
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
                    if (tasksMap.containsKey(seq)) {
                        tasksMap.get(seq).cancel(false);     // Cancel timer of this packet.
                    }
                    System.out.println("Received ACK, sequence number = " + seq);
                }
            } catch (IOException e) {
                if (!running) {
                    break;
                }
                running = false;
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
