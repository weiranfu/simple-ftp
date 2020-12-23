public class SimpleFTPClient {

    public static void main(String[] args) {
        FTPSender sender = new FTPSender("localhost", 7735, "./data/rfc119.txt", 8, 200);
        new Thread(sender).start();
    }
}
