
public class GoBackNReceiverWindow {

    private int next = 0;          // starting sequence number is 0 by default.

    /**
     * Determine whether window should receive this packet.
     * @param seq the index/sequence number of packet
     */
    public synchronized boolean shouldReceivedPacket(int seq) {
        return next == seq;
    }

    /**
     * Receives a packet.
     */
    public synchronized void receivePacket() {
        next++;
    }
}
