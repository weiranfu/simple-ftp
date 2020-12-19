/**
 * Parse the segment received or prepare a new segment.
 */
public class Segment {
    private byte[] segment;
    private static final int MASK8 = 0xFF;

    public static final int HEADER_SIZE = 8;

    public Segment(byte[] segment) {
        this.segment = segment;
    }

    public void setSeqNum(int n) {
        segment[3] = (byte) (n & MASK8);
        n >>= 8;
        segment[2] = (byte) (n & MASK8);
        n >>= 8;
        segment[1] = (byte) (n & MASK8);
        n >>= 8;
        segment[0] = (byte) (n & MASK8);
    }

    /*
     Java doesn't have unsigned byte,
     so we must use (byte & 0xFF) to get the original int value.
     */
    public int getSeqNum() {
        int seq = segment[0] & MASK8;
        seq <<= 8;
        seq |= segment[1] & MASK8;
        seq <<= 8;
        seq |= segment[2] & MASK8;
        seq <<= 8;
        seq |= segment[3] & MASK8;
        return seq;
    }

    public void setCheckSum(short checkSum) {
        segment[5] = (byte) (checkSum & MASK8);
        checkSum >>= 8;
        segment[4] = (byte) (checkSum & MASK8);
    }

    public short getCheckSum() {
        int checkSum = segment[4] & MASK8;
        checkSum <<= 8;
        checkSum |= segment[5] & MASK8;
        return (short) checkSum;
    }

    public void setType(short type) {
        segment[7] = (byte) (type & MASK8);
        type >>= 8;
        segment[6] = (byte) (type & MASK8);
    }

    public short getType() {
        int type = segment[6] & MASK8;
        type <<= 8;
        type |= segment[7] & MASK8;
        return (short) type;
    }

    /**
     * Set data of segment.
     * Copy data[] from offset with length to segment[] from 8 to end.
     * @param data  data array
     * @param offset offset of data in data array
     * @param length length of data
     */
    public void setData(byte[] data, int offset, int length) {
        System.arraycopy(data, offset, segment, HEADER_SIZE, length);
    }

    public byte[] getData() {
        int dataLength = segment.length - HEADER_SIZE;
        byte[] data = new byte[dataLength];
        System.arraycopy(segment, HEADER_SIZE, data, 0, dataLength);
        return data;
    }

    public byte[] toByteArray() {
        return segment;
    }
}
