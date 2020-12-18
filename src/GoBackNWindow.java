import java.net.DatagramPacket;

public class GoBackNWindow {
    private final int N;
    private int left, right; // left, right bound of window
    private int next;            // the index of next packet to be sent

    public GoBackNWindow(DatagramPacket[] packets, int window) {
        N = packets.length;
        left = 0; right = window - 1;
        next = 0;
    }

    /**
     * Shrinks window if window is available.
     * @return the index/sequence number of packet.
     */
    public synchronized int shrink() {
        return next <= right ? next++ : -1;
    }

    /**
     * Extends window if there are remaining packets.
     */
    public synchronized void extend() {
        if (right < N - 1) {
            right++;
        }
    }

    /**
     * Reset window if timeout.
     */
    public synchronized void reset() {
        next = left;
    }

    /**
     * Receives an ACK.
     * @param seq the index/sequence number of ACK.
     */
    public synchronized void receiveACK(int seq) {
        left = seq;
        if (next < left) next = left;
    }
}
