public class SimpleFTPServer {

    public static void main(String[] args) {
        FTPReceiver receiver = new FTPReceiver("./data/result.txt", 0.5);
        new Thread(receiver).start();
    }
}
