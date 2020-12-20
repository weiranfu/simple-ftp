import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Future;

public class SenderTimeoutTask extends TimerTask {

    private int seq;
    private GoBackNSenderWindow window;
    private Map<Integer, Future<?>> tasksMap;

    public SenderTimeoutTask(int seq, GoBackNSenderWindow window, Map<Integer, Future<?>> tasksMap) {
        this.seq = seq;
        this.window = window;
        this.tasksMap = tasksMap;
    }

    @Override
    public void run() {
        System.out.println("Timeout, sequence number = " + seq);
        int[] interval = window.resetWindow();
        for (int i = interval[0]; i <= interval[1]; i++) {
            if (tasksMap.containsKey(i)) {
                tasksMap.get(i).cancel(false);                 // Cancel the timers of packets which need to be retransmitted.
            }
        }
    }
}
