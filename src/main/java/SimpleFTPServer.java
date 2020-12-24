import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "ftp-server", mixinStandardHelpOptions = true, version = "ftp-server 1.0",
description = "A simple FTP server cli.")
public class SimpleFTPServer implements Runnable {

    @Option(names = {"-f", "--file"}, paramLabel = "<file path>", required = true, description = "The path of output file.")
    private String filePath;
    @Option(names = {"-p", "--possibility"}, paramLabel = "<loss possibility>", required = true, description = "The loss possibility of a packet.")
    private double p;

    @Override
    public void run() {
        FTPReceiver receiver = new FTPReceiver(filePath, p);
        new Thread(receiver).start();
    }

    public static void main(String[] args) {
        new CommandLine(new SimpleFTPServer()).execute(args);
    }
}
