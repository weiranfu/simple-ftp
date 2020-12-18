import java.net.DatagramPacket;

public class GoBackNSenderWindow {
    private final int N;
    private int left, right; // left, right bound of window
    private int next;            // the index of next packet to be sent

    public GoBackNSenderWindow(DatagramPacket[] packets, int window) {
        N = packets.length;
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
     * Reset window if timeout.
     */
    public synchronized void resetWindow() {
        next = left;
    }

    /**
     * @return whether packets are all sent.
     */
    public synchronized boolean isFinished() {
        return left > right;
    }
}
