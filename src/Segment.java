public class Segment {
    private int N;
    private byte[] bytes;
    private static final int HEADER_BYTE = 8;
    private static final int MASK8 = 0xFF;

    public Segment(int MSS) {
        N = MSS + HEADER_BYTE;
        bytes = new byte[N];
    }

    public void setSeqNum(int n) {
        bytes[3] = (byte) (n & MASK8);
        n >>= 8;
        bytes[2] = (byte) (n & MASK8);
        n >>= 8;
        bytes[1] = (byte) (n & MASK8);
        n >>= 8;
        bytes[0] = (byte) (n & MASK8);
    }

    /*
     Java doesn't have unsigned byte,
     so we must use (byte & 0xFF) to get the original int value.
     */
    public int getSeqNum() {
        int seq = bytes[0] & MASK8;
        seq <<= 8;
        seq |= bytes[1] & MASK8;
        seq <<= 8;
        seq |= bytes[2] & MASK8;
        seq <<= 8;
        seq |= bytes[3] & MASK8;
        return seq;
    }

    public void setCheckSum(int checkSum) {
        bytes[5] = (byte) (checkSum & MASK8);
        checkSum >>= 8;
        bytes[4] = (byte) (checkSum & MASK8);
    }

    public int getCheckSum() {
        int checkSum = bytes[4] & MASK8;
        checkSum <<= 8;
        checkSum |= bytes[5] & MASK8;
        return checkSum;
    }

    public void setType(int type) {
        bytes[7] = (byte) (type & MASK8);
        type >>= 8;
        bytes[6] = (byte) (type & MASK8);
    }

    public int getType() {
        int type = bytes[6] & MASK8;
        type <<= 8;
        type |= bytes[7] & MASK8;
        return type;
    }

    public byte[] toBytes() {
        return bytes;
    }
}
