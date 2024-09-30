package fr.lernejo.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvReaderTest {

    @TempDir
    Path tempDir;

    private String createTempCsvFile(String content) throws IOException {
        Path file = tempDir.resolve("test.csv");
        Files.write(file, content.getBytes());
        return file.toString();
    }

    private String captureSystemOut(Runnable runnable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        runnable.run();
        System.out.flush();
        System.setOut(old);
        return baos.toString().trim();
    }

    @Test
    void testTemperatureAvg() throws IOException {
        String csvContent = "Header1\nHeader2\nHeader3\nHeader4\n" +
            "2023-01-01T00:00,10.5,0,1000.0,0,5.0,0,0,500.0\n" +
            "2023-01-01T12:00,15.5,0,1005.0,0,7.0,0,1,700.0\n";
        String filePath = createTempCsvFile(csvContent);

        String output = captureSystemOut(() ->
            CsvReader.main(new String[]{filePath, "2023-01-01", "2023-01-02", "temperature_2m", "DAY", "AVG"})
        );

        assertEquals("15.500000000000000 °C", output);
    }

    @Test
    void testPressureSum() throws IOException {
        String csvContent = "Header1\nHeader2\nHeader3\nHeader4\n" +
            "2023-01-01T00:00,10.5,0,1000.0,0,5.0,0,0,500.0\n" +
            "2023-01-01T12:00,15.5,0,1005.0,0,7.0,0,1,700.0\n";
        String filePath = createTempCsvFile(csvContent);

        String output = captureSystemOut(() ->
            CsvReader.main(new String[]{filePath, "2023-01-01", "2023-01-02", "pressure_msl", "NIGHT", "SUM"})
        );

        assertEquals("1000.0 hPa", output);
    }

    @Test
    void testWindSpeedMin() throws IOException {
        String csvContent = "Header1\nHeader2\nHeader3\nHeader4\n" +
            "2023-01-01T00:00,10.5,0,1000.0,0,5.0,0,0,500.0\n" +
            "2023-01-01T12:00,15.5,0,1005.0,0,7.0,0,1,700.0\n";
        String filePath = createTempCsvFile(csvContent);

        String output = captureSystemOut(() ->
            CsvReader.main(new String[]{filePath, "2023-01-01", "2023-01-02", "wind_speed_10m", "NIGHT", "MIN"})
        );

        assertEquals("5.0 km/h", output);
    }

    @Test
    void testIrradianceMax() throws IOException {
        String csvContent = "Header1\nHeader2\nHeader3\nHeader4\n" +
            "2023-01-01T00:00,10.5,0,1000.0,0,5.0,0,0,500.0\n" +
            "2023-01-01T12:00,15.5,0,1005.0,0,7.0,0,1,700.0\n";
        String filePath = createTempCsvFile(csvContent);

        String output = captureSystemOut(() ->
            CsvReader.main(new String[]{filePath, "2023-01-01", "2023-01-02", "direct_normal_irradiance_instant", "DAY", "MAX"})
        );

        assertEquals("700.0 W/m²", output);
    }

    @Test
    void testLargeNumberFormatting() throws IOException {
        String csvContent = "Header1\nHeader2\nHeader3\nHeader4\n" +
            "2023-01-01T00:00,10.5,0,10000000.0,0,5.0,0,0,500.0\n";
        String filePath = createTempCsvFile(csvContent);

        String output = captureSystemOut(() ->
            CsvReader.main(new String[]{filePath, "2023-01-01", "2023-01-02", "pressure_msl", "NIGHT", "SUM"})
        );

        assertEquals("1.00000000E7 hPa", output);
    }
}
