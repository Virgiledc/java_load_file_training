package fr.lernejo.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderTest {

    @Test
    void testGetColumnIndex() {
        assertEquals(1, CsvReader.getColumnIndex("temperature_2m"));
        assertEquals(3, CsvReader.getColumnIndex("pressure_msl"));
        assertEquals(5, CsvReader.getColumnIndex("wind_speed_10m"));
        assertEquals(8, CsvReader.getColumnIndex("direct_normal_irradiance_instant"));
        assertThrows(IllegalArgumentException.class, () -> CsvReader.getColumnIndex("invalid_metric"));
    }

    @Test
    void testGetUnit() {
        assertEquals("°C", CsvReader.getUnit("temperature_2m"));
        assertEquals("hPa", CsvReader.getUnit("pressure_msl"));
        assertEquals("km/h", CsvReader.getUnit("wind_speed_10m"));
        assertEquals("W/m²", CsvReader.getUnit("direct_normal_irradiance_instant"));
        assertThrows(IllegalArgumentException.class, () -> CsvReader.getUnit("invalid_metric"));
    }

    @Test
    void testAggregate(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, """
            header1,header2,header3
            header4,header5,header6
            header7,header8,header9
            time,temperature_2m (°C),is_day ()
            2022-01-01T00:00,10.0,0
            2022-01-01T01:00,11.0,0
            2022-01-01T12:00,20.0,1
            2022-01-02T00:00,9.0,0
            """);

        String[] args = {csvFile.toString(), "2022-01-01", "2022-01-02", "temperature_2m", "NIGHT", "AVG"};
        CsvReader.main(args);
        // Add assertions to check the output
    }

    // Add more tests to cover different scenarios and edge cases
}
