import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class ACKReceiveThread implements Runnable {

    private final DatagramSocket socket;
    private final GoBackNSenderWindow window;
    private final int MSS;
    private final Map<Integer, Future<?>> tasksMap;
    private boolean running;
    private AtomicBoolean waiting;
    private WaitNotify waitNotify;

    public ACKReceiveThread(DatagramSocket socket, GoBackNSenderWindow window, int MSS, Map<Integer, Future<?>> tasksMap, AtomicBoolean waiting, WaitNotify waitNotify) {
        this.socket = socket;
        this.window = window;
        this.MSS = MSS;
        this.tasksMap = tasksMap;
        this.waiting = waiting;
        this.waitNotify = waitNotify;
    }

    @Override
    public void run() {
        byte[] buf = new byte[MSS];
        DatagramPacket packet = new DatagramPacket(buf, MSS);
        running = true;
        while (running) {
            try {
                socket.receive(packet);
                waiting.set(true);                                          // Let FTPSender thread to wait.
                byte[] data = new byte[packet.getLength()];    // for storing data.
                System.arraycopy(buf, 0, data, 0, packet.getLength());
                Segment segment = new Segment(data);
                short type = segment.getType();
                if (type == Segment.ackType) {
                    int ACKSeq = segment.getSeqNum();
                    window.receiveACK(ACKSeq);
                    for (Integer key : tasksMap.keySet()) {
                        if (key < ACKSeq) {
                            tasksMap.get(key).cancel(false);  // Cancel timer of this packet.
                            tasksMap.remove(key);
                        }
                    }
                    System.out.println("Received ACK, sequence number = " + ACKSeq);
                }
            } catch (IOException e) {
                if (!running) {
                    break;
                }
                running = false;
                System.out.println("Failed to receive packet from server: " + e.getMessage());
            } finally {
                waiting.set(false);
                waitNotify.doNotify();                  // Notify FTPSender thread to continue.
            }
        }
    }

    public void stop() {
        running = false;
        socket.close();
        System.out.println("Receiving ACK Thread is closed.");
    }
}
