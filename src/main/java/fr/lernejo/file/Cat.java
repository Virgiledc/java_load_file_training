package fr.lernejo.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Cat {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Missing argument");
            System.exit(3);
        } else if (args.length > 1) {
            System.out.println("Too many arguments");
            System.exit(4);
        }

        Path filePath = Path.of(args[0]);
        if (!Files.exists(filePath)) {
            System.out.println("File not found");
            System.exit(5);
        }

        if (Files.isDirectory(filePath)) {
            System.out.println("A file is required");
            System.exit(6);
        }

        try {
            long fileSize = Files.size(filePath);
            if (fileSize > 3 * 1024) {
                System.out.println("File too large");
                System.exit(7);
            }

            String content = Files.readString(filePath);
            System.out.println(content);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }
}
