import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class FTPReceiver implements Runnable {

    private static int SERVER_PORT = 7735;
    private final int MSS = 1460;               // MSS of Ethernet.
    private final int UDP_HEADER_SIZE = 8;
    private final int MASK16 = 0xFFFF;
    private final short CHECKSUM_ANS = (short)-1; // 1111111111111111

    private final byte[] buf;
    private final String FILE_PATH;
    private final double LOST_POSSIBILITY;
    private final PseudoHeader pseudoHeader;
    private final GoBackNReceiverWindow window;

    private DatagramSocket socket;
    private InetAddress serverAddress;

    public static final int TRANSFER_COMPLETED_SEQ = -1;

    public FTPReceiver(String filename, double p) {
        this(SERVER_PORT, filename, p);
    }

    public FTPReceiver(int serverPort, String filename, double p) {
        SERVER_PORT = serverPort;
        FILE_PATH = filename;
        LOST_POSSIBILITY = p;
        pseudoHeader = new PseudoHeader();
        window = new GoBackNReceiverWindow();
        buf = new byte[MSS];
    }


    @Override
    public void run() {
        try {
            socket = new DatagramSocket(SERVER_PORT);
            serverAddress = InetAddress.getByName("localhost");
            pseudoHeader.setDesIP(serverAddress);
            pseudoHeader.setProtocol("UDP");
            List<byte[]> data = rdt_receive(socket, window);
            writeToFile(data, FILE_PATH);
        } catch (SocketException e) {
            System.out.println("Failed to open UDP socket at port " + SERVER_PORT + ": " + e.getMessage());
        } catch (UnknownHostException e) {
            System.out.println("Failed to find the address of localhost: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to received file with socket or write file to disk: " + e.getMessage());
        }
    }

    /**
     * Reliable receive data with UDP and Go-back-N.
     */
    private List<byte[]> rdt_receive(DatagramSocket socket, GoBackNReceiverWindow window) throws IOException {
        List<byte[]> dataByteArray = new ArrayList<>();
        Segment ACKSegment = new Segment(new byte[Segment.HEADER_SIZE]);
        ACKSegment.setType(Segment.ackType);
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (true) {
            socket.receive(packet);
            byte[] segmentByteArray = new byte[packet.getLength()];
            System.arraycopy(buf, 0, segmentByteArray, 0, packet.getLength());
            Segment segment = new Segment(segmentByteArray);
            short type = segment.getType();
            if (type == Segment.dataType) {
                int seq = segment.getSeqNum();
                if (seq == TRANSFER_COMPLETED_SEQ) {     // transfer completed.
                    break;
                }
                if (!window.shouldReceivedPacket(seq)) continue;  // the segment received is out of order!
                byte[] data = segment.getData();
                pseudoHeader.setSourceIP(packet.getAddress());
                pseudoHeader.setTotalLength(packet.getLength() + UDP_HEADER_SIZE + PseudoHeader.HEADER_SIZE);
                if (checkPacket(segment, pseudoHeader, data)) {
                    System.out.println("Received packet, segment number = " + seq);
                    window.receivePacket();
                    dataByteArray.add(data);
                    ACKSegment.setSeqNum(seq + 1);          // ACK with seq + 1
                    byte[] ACKByteArray = ACKSegment.toByteArray();
                    DatagramPacket ACKPacket = new DatagramPacket(ACKByteArray, ACKByteArray.length, packet.getAddress(), packet.getPort());
                    socket.send(ACKPacket);                 // Send ACK
                    System.out.println("Send ACK, sequence number " + (seq + 1));
                }
            }
        }
        return dataByteArray;
    }

    /**
     * Check whether this segment is valid according to its checksum.
     * @param segment The segment to be checked.
     * @param pseudoHeader The pseudoHeader of this segment.
     * @param data The data in segment.
     */
    private boolean checkPacket(Segment segment, PseudoHeader pseudoHeader, byte[] data) {
        short sum = 0;
        sum += pseudoHeader.getSourceIPCheckSum();
        sum += pseudoHeader.getDesIPCheckSum();
        sum += pseudoHeader.getProtocol();
        sum += pseudoHeader.getTotalLength();
        sum += (segment.getSeqNum() << 16) & MASK16;
        sum += segment.getSeqNum() & MASK16;
        sum += segment.getType();
        for (int i = 0; i < data.length; i += 2) {
            byte first = data[i];
            byte second = i != data.length - 1 ? data[i + 1] : 0;
            sum += (short) ((first << 8) | second);
        }
        return (short) (sum + segment.getCheckSum()) == CHECKSUM_ANS;
    }

    private void writeToFile(List<byte[]> list, String filename) throws IOException {
        int len = 0;
        for (byte[] bytes : list) {
            len += bytes.length;
        }
        byte[] data = new byte[len];
        int p = 0;
        for (byte[] bytes : list) {
            for (byte b : bytes) {
                data[p++] = b;
            }
        }
        FileDriver.writeFile(data, filename);
    }
}
