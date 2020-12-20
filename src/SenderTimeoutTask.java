import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class SenderTimeoutTask extends TimerTask {

    private int seq;
    private GoBackNSenderWindow window;
    private Map<Integer, Future<?>> tasksMap;
    private AtomicBoolean waiting;
    private WaitNotify waitNotify;

    public SenderTimeoutTask(int seq, GoBackNSenderWindow window, Map<Integer, Future<?>> tasksMap, AtomicBoolean waiting, WaitNotify waitNotify) {
        this.seq = seq;
        this.window = window;
        this.tasksMap = tasksMap;
        this.waiting = waiting;
        this.waitNotify = waitNotify;
    }

    @Override
    public void run() {
        waiting.set(true);                                      // Let FTPSender thread to wait
        window.resetWindow();
        for (Integer key : tasksMap.keySet()) {
            tasksMap.get(key).cancel(false);               // Cancel the timers of packets which need to be retransmitted.
            tasksMap.remove(key);
        }
        System.out.println("Timeout, sequence number = " + seq);
        waiting.set(false);
        waitNotify.doNotify();                                  // Notify FTPSender thread to continue
    }
}
