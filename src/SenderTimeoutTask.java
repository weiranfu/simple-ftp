import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SenderTimeoutTask extends TimerTask {

    private int seq;
    private GoBackNSenderWindow window;
    private List<Timer> timers;

    public SenderTimeoutTask(int seq, GoBackNSenderWindow window, List<Timer> timers) {
        this.seq = seq;
        this.window = window;
        this.timers = timers;
    }

    @Override
    public void run() {
        System.out.println("Timeout, sequence number = " + seq);
        int[] interval = window.resetWindow();
        for (int i = interval[0]; i <= interval[1]; i++) {
            timers.get(i).cancel();                 // Cancel the timers of packets which need to be retransmitted.
        }
    }
}
