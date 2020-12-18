import java.net.DatagramPacket;

public class GoBackNReceiverWindow {
    private final int N;
    private int left, right;

    public GoBackNReceiverWindow(DatagramPacket[] packets, int windowSize) {
        N = packets.length;
        left = 0; right = N - 1;
    }

    /**
     * Determine whether window should receive this packet.
     * @param seq the index/sequence number of packet
     */
    public synchronized boolean shouldReceivedPacket(int seq) {
        return left == seq;
    }

    /**
     * Receives a packet.
     */
    public synchronized void receivePacket() {
        if (left <= right) {
            left++;
        }
        if (right < N - 1) {   // if right bound doesn't exceed N - 1
            right++;
        }
    }

    /**
     * Determine whether all packets are received.
     */
    public synchronized boolean isFinished() {
        return left > right;
    }
}
