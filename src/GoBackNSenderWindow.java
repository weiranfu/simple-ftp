
public class GoBackNSenderWindow {
    private final int N;
    private int left, right; // left, right bound of window
    private int next;            // the index of next packet to be sent

    public GoBackNSenderWindow(int packetsNum, int window) {
        N = packetsNum;
        left = 0; right = window - 1;
        next = 0;
    }

    /**
     * Shrinks window if window is available.
     * @return the index/sequence number of packet.
     */
    public synchronized int sendPacketSeq() {
        return next <= right ? next++ : -1;
    }

    /**
     * Receives an ACK.
     * @param seq the index/sequence number of ACK.
     */
    public synchronized void receiveACK(int seq) {
        int len = seq - left;
        right += len;
        if (right >= N) right = N - 1;  // if right bound exceeds N
        left = seq;
        if (next < left) next = left;
    }

    /**
     * Reset window to the left bound if timeout.
     * @return An array of 2 size: res[0] The reset index/sequence of packet. res[1] The last index/sequence of already sent packet.
     */
    public synchronized int[] resetWindow() {
        int[] res = new int[2];
        res[1] = next - 1;
        res[0] = left;
        next = left;
        return res;
    }

    /**
     * Determine whether all packets are sent.
     */
    public synchronized boolean isFinished() {
        return left > right;
    }
}
