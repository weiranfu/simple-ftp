import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class File {

    public static byte[] readFile(String filePath) throws IOException {
        Path file = Paths.get(filePath);
        return Files.readAllBytes(file);
    }

    public static void writeFile(byte[] bytes, String filePath) throws IOException {
        Path file = Paths.get(filePath);
        Files.write(file, bytes);
    }
}
