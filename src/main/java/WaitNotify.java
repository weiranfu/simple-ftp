public class WaitNotify {

    private final Object object = new Object();
    private boolean wasSignalled = false;

    public void doWait(){
        synchronized(object){
            while(!wasSignalled){
                try{
                    object.wait();
                } catch(InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            //clear signal and continue running.
            wasSignalled = false;
        }
    }

    public void doNotify(){
        synchronized(object){
            wasSignalled = true;
            object.notify();
        }
    }
}
