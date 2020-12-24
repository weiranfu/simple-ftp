import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ftp-client", mixinStandardHelpOptions = true, version = "ftp-client 1.0",
description = "A simple FTP client cli.")
public class SimpleFTPClient implements Runnable {

    @Option(names = {"-H", "--hostname"}, paramLabel = "<hostname>", required = true, description = "The hostname of FTP server.")
    private String hostname;
    @Option(names = {"-p", "--port"}, paramLabel = "<port number>", required = true, description = "The port number of FTP server is listening on.")
    private int port;
    @Option(names = {"-f", "--file"}, paramLabel = "<file path>", required = true, description = "The path of file sent to FTP server.")
    private String filePath;
    @Option(names = {"-w", "--window"}, paramLabel = "<window size>", defaultValue = "8", description = "The sliding window size of Go-back-N algorithm.")
    private int windowSize;
    @Option(names = {"-m", "--mss"}, paramLabel = "<MSS>", defaultValue = "500", description = "The Maximum Segment Size(MSS) used for UDP segment.")
    private int MSS;

    @Override
    public void run() {
        FTPSender sender = new FTPSender(hostname, port, filePath, windowSize, MSS);
        new Thread(sender).start();
    }

    public static void main(String[] args) {
        new CommandLine(new SimpleFTPClient()).execute(args);
    }
}
