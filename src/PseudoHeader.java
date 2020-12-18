import java.net.InetAddress;

public class PseudoHeader {
    private static final int MASK8 = 0xFF;
    private final int HEADER_SIZE = 12;
    private final int SOURCE_IP_SIZE = 4;
    private final int DESTINATION_IP_SIZE = 4;
    private final int RESERVED_SIZE = 1;
    private final int PROTOCOL_SIZE = 1;
    private final int TOTAL_LENGTH_SIZE = 2;
    private final int UDP = 17;
    private final byte[] pseudoHeader;

    public PseudoHeader() {
        pseudoHeader = new byte[HEADER_SIZE];
    }

    public void setSourceIP(InetAddress localAddress) {
        byte[] address = localAddress.getAddress();
        System.arraycopy(address, 0, pseudoHeader, 0, SOURCE_IP_SIZE);
    }

    public byte[] getSourceIP() {
        byte[] address = new byte[SOURCE_IP_SIZE];
        System.arraycopy(pseudoHeader, 0, address, 0, SOURCE_IP_SIZE);
        return address;
    }

    public void setDesIP(InetAddress serverAddress) {
        byte[] address = serverAddress.getAddress();
        System.arraycopy(address, 0, pseudoHeader, SOURCE_IP_SIZE, DESTINATION_IP_SIZE);
    }

    public byte[] getDesIP() {
        byte[] address = new byte[DESTINATION_IP_SIZE];
        System.arraycopy(pseudoHeader, SOURCE_IP_SIZE, address, 0, DESTINATION_IP_SIZE);
        return address;
    }

    public byte getReserved() {
        return 0;
    }

    public void setProtocol(String protocol) {
        if (protocol.equals("UDP")) {
            pseudoHeader[SOURCE_IP_SIZE + DESTINATION_IP_SIZE + RESERVED_SIZE] = (byte) UDP;
        }
    }

    public byte getProtocol() {
        return pseudoHeader[SOURCE_IP_SIZE + DESTINATION_IP_SIZE + RESERVED_SIZE];
    }

    public void setTotalLength(int length) {
        int index = SOURCE_IP_SIZE + DESTINATION_IP_SIZE + RESERVED_SIZE + PROTOCOL_SIZE;
        pseudoHeader[index + 1] = (byte) (length & MASK8);
        length >>= 8;
        pseudoHeader[index] = (byte) (length & MASK8);
    }

    public byte[] getTotalLength() {
        byte[] length = new byte[TOTAL_LENGTH_SIZE];
        int index = SOURCE_IP_SIZE + DESTINATION_IP_SIZE + RESERVED_SIZE + PROTOCOL_SIZE;
        System.arraycopy(pseudoHeader, index, length, 0, TOTAL_LENGTH_SIZE);
        return length;
    }
}
