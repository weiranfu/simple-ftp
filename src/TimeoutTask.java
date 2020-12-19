import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutTask extends TimerTask {

    private int seq;
    private GoBackNSenderWindow window;
    private List<Timer> timers;

    public TimeoutTask(int seq, GoBackNSenderWindow window, List<Timer> timers) {
        this.seq = seq;
        this.window = window;
        this.timers = timers;
    }

    @Override
    public void run() {
        System.out.println("Timeout, sequence number = " + seq);
        int from = window.resetWindow(seq);
        for (int i = seq + 1; i <= from; i++) {
            timers.get(i).cancel();                 // cancel the timers of packets which need to be retransmitted.
        }
    }
}
