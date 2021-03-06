import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class FTPSender implements Runnable {

    private final int UDP_HEADER_SIZE = 8;
    private final int MASK16 = 0xFFFF;
    private final int DELAY_SECONDS = 3;

    private final String SERVER_HOSTNAME;
    private final int SERVER_PORT;
    private final String FILE_PATH;
    private final int WINDOW_SIZE;
    private final int MSS;
    private final PseudoHeader pseudoHeader;
    private final ScheduledThreadPoolExecutor executor;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private InetAddress localAddress;
    private GoBackNSenderWindow window;
    private AtomicBoolean waiting;
    private WaitNotify waitNotify;


    public FTPSender(String serverHostname, int serverPort, String filePath, int windowSize, int MSS) {
        SERVER_HOSTNAME = serverHostname;
        SERVER_PORT = serverPort;
        FILE_PATH = filePath;
        WINDOW_SIZE = windowSize;
        this.MSS = MSS;
        pseudoHeader = new PseudoHeader();
        executor = new ScheduledThreadPoolExecutor(windowSize);
        executor.setRemoveOnCancelPolicy(true);         // Remove cancelled task immediately
        waiting = new AtomicBoolean();
        waitNotify = new WaitNotify();
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            localAddress = InetAddress.getByName("localhost");
            serverAddress = InetAddress.getByName(SERVER_HOSTNAME);
            pseudoHeader.setSourceIP(localAddress);
            pseudoHeader.setDesIP(serverAddress);
            pseudoHeader.setProtocol("UDP");
            List<Segment> segments = prepareSegments();
            Map<Integer, Future<?>> tasksMap = new ConcurrentHashMap<>();           // Use ConcurrentHashMap for concurrency.
            window = new GoBackNSenderWindow(segments.size(), WINDOW_SIZE);
            ACKReceiveThread receiveThread = new ACKReceiveThread(socket, window, MSS, tasksMap, waiting, waitNotify);
            new Thread(receiveThread).start();
            // Begin to send UDP segments.
            waiting.set(false);
            rdt_send(segments, tasksMap, socket);
            receiveThread.stop();
        } catch (SocketException e) {
            System.out.println("Failed to open a UDP socket: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println("Failed to find the address of localhost: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to load file or send file with socket: " + e.getMessage());
        } finally {
            socket.close();
            executor.shutdownNow();             // Shut down all timers immediately.
        }
    }

    /**
     * Reliable send data with UDP and Go-back-N.
     * @param segments The data wrapped in Segment.
     * @param tasksMap The timer Map for each segment and its future task.
     * @param socket The UDP socket.
     */
    private void rdt_send(List<Segment> segments, Map<Integer, Future<?>> tasksMap, DatagramSocket socket) throws IOException {
        while (!window.isFinished()) {
            while (waiting.get()) {         // wait for timeout thread finishing clearing timers.
                waitNotify.doWait();
            }
            int seq = window.sendPacketSeq();
            if (seq != -1) {
                byte[] data = segments.get(seq).toByteArray();
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
                socket.send(packet);
                SenderTimeoutTask timeoutTask = new SenderTimeoutTask(seq, window, tasksMap, waiting, waitNotify);
                Future<?> task = executor.schedule(timeoutTask, DELAY_SECONDS, TimeUnit.SECONDS);
                if (tasksMap.containsKey(seq)) {
                    tasksMap.get(seq).cancel(false);
                }
                tasksMap.put(seq, task);
                System.out.println("Sent packet, sequence number = " + seq);
            }
        }
        Segment msg = new Segment(new byte[Segment.HEADER_SIZE]);
        msg.setSeqNum(FTPReceiver.TRANSFER_COMPLETED_SEQ);
        msg.setType(Segment.dataType);
        byte[] data = msg.toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
        socket.send(packet);
        System.out.println("File transfer completed.");
    }

    private List<Segment> prepareSegments() throws IOException {
        byte[] data = FileDriver.readFile(FILE_PATH);
        List<Segment> segments = new ArrayList<>();
        int remaining = data.length;
        int seqNum = 0;
        int offset = 0;
        while (remaining != 0) {
            int len = Math.min(remaining, MSS);
            byte[] segmentByteArray = new byte[len + Segment.HEADER_SIZE];
            Segment segment = new Segment(segmentByteArray);
            segment.setSeqNum(seqNum++);
            segment.setType(Segment.dataType);
            segment.setData(data, offset, len);
            pseudoHeader.setTotalLength(len + Segment.HEADER_SIZE + UDP_HEADER_SIZE + PseudoHeader.HEADER_SIZE);
            segment.setCheckSum(calCheckSum(segment, pseudoHeader, data, offset, len));
            segments.add(segment);
            offset += len;
            remaining -= len;
        }
        return segments;
    }

    private short calCheckSum(Segment segment, PseudoHeader pseudoHeader, byte[] data, int offset, int length) {
        short sum = 0;
        sum += pseudoHeader.getSourceIPCheckSum();
        sum += pseudoHeader.getDesIPCheckSum();
        sum += pseudoHeader.getProtocol();
        sum += pseudoHeader.getTotalLength();
        sum += (segment.getSeqNum() >> 16) & MASK16;
        sum += segment.getSeqNum() & MASK16;
        sum += segment.getType();
        for (int i = offset; i < offset + length; i += 2) {
            byte first = data[i];
            byte second = i != offset + length - 1 ? data[i + 1] : 0;
            sum += (short)((first << 8) | second);
        }
        return (short)~sum;
    }
}
