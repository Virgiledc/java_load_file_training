package fr.lernejo.file;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CsvReaderTest {
    public static void main(String[] args) throws Exception {
        testTemperatureAvg();
        testPressureSum();
        testWindSpeedMin();
        testIrradianceMax();
    }

    private static void testTemperatureAvg() throws Exception {
        runTest("temperature_2m", "2023-01-01", "2023-01-31", "DAY", "AVG", "10.5");
    }

    private static void testPressureSum() throws Exception {
        runTest("pressure_msl", "2023-01-01", "2023-12-31", "NIGHT", "SUM", "1.5e+07");
    }

    private static void testWindSpeedMin() throws Exception {
        runTest("wind_speed_10m", "2023-06-01", "2023-06-30", "DAY", "MIN", "2.3");
    }

    private static void testIrradianceMax() throws Exception {
        runTest("direct_normal_irradiance_instant", "2023-06-01", "2023-08-31", "DAY", "MAX", "950.0");
    }

    private static void runTest(String metric, String startDate, String endDate, String selector, String aggregationType, String expectedOutput) throws Exception {
        String csvContent = generateCsvContent(metric, LocalDate.parse(startDate), LocalDate.parse(endDate));
        Path tempFile = Files.createTempFile("test", ".csv");
        Files.writeString(tempFile, csvContent);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        CsvReader.main(new String[]{tempFile.toString(), startDate, endDate, metric, selector, aggregationType});

        System.out.flush();
        System.setOut(old);

        String output = baos.toString().trim();

        if (output.startsWith(expectedOutput)) {
            System.out.println("Test passed for " + metric + " " + aggregationType + ": " + output);
        } else {
            System.out.println("Test failed for " + metric + " " + aggregationType + ". Expected: " + expectedOutput + ", but was: " + output);
        }

        Files.delete(tempFile);
    }

    private static String generateCsvContent(String metric, LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("Header line 1\n");
        sb.append("Header line 2\n");
        sb.append("Header line 3\n");
        sb.append("Header line 4\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        double baseValue = switch (metric) {
            case "temperature_2m" -> 10.0;
            case "pressure_msl" -> 1000.0;
            case "wind_speed_10m" -> 5.0;
            case "direct_normal_irradiance_instant" -> 500.0;
            default -> throw new IllegalArgumentException("Invalid metric");
        };

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (int hour = 0; hour < 24; hour++) {
                String datetime = date.atTime(hour, 0).format(formatter);
                double temperature = baseValue + Math.random() * 10 - 5;
                double pressure = 1000 + Math.random() * 50 - 25;
                double windSpeed = 5 + Math.random() * 10;
                double irradiance = (hour >= 6 && hour < 18) ? 500 + Math.random() * 500 : 0;
                String isDay = (hour >= 6 && hour < 18) ? "1" : "0";
                sb.append(String.format("%s,%.1f,0,%.1f,0,%.1f,0,%s,%.1f\n",
                    datetime, temperature, pressure, windSpeed, isDay, irradiance));
            }
        }

        return sb.toString();
    }
}
