package fr.lernejo.file;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatTest {

    @Test
    public void testMissingArgument() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {};
        Cat.main(args);

        assertEquals("Missing argument\n", outContent.toString());
    }

    @Test
    public void testTooManyArguments() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"file1.txt", "file2.txt"};
        Cat.main(args);

        assertEquals("Too many arguments\n", outContent.toString());
    }

    @Test
    public void testFileNotFound() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"non_existent_file.txt"};
        Cat.main(args);

        assertEquals("File not found\n", outContent.toString());
    }

    @Test
    public void testFileIsDirectory() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        File dir = new File("test_directory");
        dir.mkdir();

        String[] args = {"test_directory"};
        Cat.main(args);

        assertEquals("A file is required\n", outContent.toString());

        dir.delete();
    }

    @Test
    public void testFileTooLarge() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Create a large file
        String largeFilePath = "large_file.txt";
        byte[] largeContent = new byte[3073];
        Files.write(Paths.get(largeFilePath), largeContent);

        String[] args = {largeFilePath};
        Cat.main(args);

        assertEquals("File too large\n", outContent.toString());

        Files.delete(Paths.get(largeFilePath));
    }

    @Test
    public void testValidFile() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String filePath = "test_file.txt";
        Files.write(Paths.get(filePath), "Hello, world!".getBytes());

        String[] args = {filePath};
        Cat.main(args);

        assertEquals("Hello, world!\n", outContent.toString());

        Files.delete(Paths.get(filePath));
    }
}
