import java.net.InetAddress;

public class PseudoHeader {
    private static final int MASK8 = 0xFF;
    private final int SOURCE_IP_SIZE = 4;
    private final int DESTINATION_IP_SIZE = 4;
    private final int RESERVED_SIZE = 1;
    private final int PROTOCOL_SIZE = 1;
    private final int TOTAL_LENGTH_SIZE = 2;
    private final int UDP = 17;
    private final byte[] pseudoHeader;

    public static final int HEADER_SIZE = 12;

    public PseudoHeader() {
        pseudoHeader = new byte[HEADER_SIZE];
    }

    public void setSourceIP(InetAddress localAddress) {
        byte[] address = localAddress.getAddress();
        System.arraycopy(address, 0, pseudoHeader, 0, SOURCE_IP_SIZE);
    }

    public short getSourceIPCheckSum() {
        short sum = 0;
        sum += (pseudoHeader[0] << 8) | pseudoHeader[1];
        sum += (pseudoHeader[2] << 8) | pseudoHeader[3];
        return sum;
    }

    public void setDesIP(InetAddress serverAddress) {
        byte[] address = serverAddress.getAddress();
        System.arraycopy(address, 0, pseudoHeader, SOURCE_IP_SIZE, DESTINATION_IP_SIZE);
    }

    public short getDesIPCheckSum() {
        short sum = 0;
        sum += (pseudoHeader[DESTINATION_IP_SIZE] << 8) | pseudoHeader[DESTINATION_IP_SIZE + 1];
        sum += (pseudoHeader[DESTINATION_IP_SIZE + 2] << 8) | pseudoHeader[DESTINATION_IP_SIZE + 3];
        return sum;
    }

    public byte getReservedCheckSum() {
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

    public short getTotalLength() {
        int index = SOURCE_IP_SIZE + DESTINATION_IP_SIZE + RESERVED_SIZE + PROTOCOL_SIZE;
        short sum = 0;
        sum += (pseudoHeader[index] << 8) | pseudoHeader[index + 1];
        return sum;
    }
}
