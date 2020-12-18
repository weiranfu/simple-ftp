public class Segment {
    private int N;
    private byte[] bytes;

    public Segment(int MSS) {
        N = MSS + 8;           // size of header is 8 bytes
        bytes = new byte[N];
    }

    public void setSeqNum(int n) {
        bytes[3] = (byte) (n & 255);
        n >>= 8;
        bytes[2] = (byte) (n & 255);
        n >>= 8;
        bytes[1] = (byte) (n & 255);
        n >>= 8;
        bytes[0] = (byte) (n & 255);
    }

    public int getSeqNum() {
        int seq = bytes[0];
        seq <<= 8;
        seq |= 255;
        seq &= bytes[1];
        seq <<= 8;
        seq |= 255;
        seq &= bytes[2];
        seq <<= 8;
        seq |= 255;
        seq &= bytes[3];
        return seq;
    }

    public void setCheckSum(int checkSum) {
        bytes[5] = (byte) (checkSum & 255);
        checkSum >>= 8;
        bytes[4] = (byte) (checkSum & 255);
    }

    public void setType(int type) {
        bytes[7] = (byte) (type & 255);
        type >>= 8;
        bytes[6] = (byte) (type & 255);
    }
}
