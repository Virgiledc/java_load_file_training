package fr.lernejo.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class CsvReader {
    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Invalid number of arguments");
            System.exit(1);
        }

        String filePath = args[0];
        LocalDate startDate = LocalDate.parse(args[1]);
        LocalDate endDate = LocalDate.parse(args[2]);
        String metric = args[3];
        String selector = args[4];
        String aggregationType = args[5];

        int columnIndex = getColumnIndex(metric);
        Function<String, Double> parseValue = getParseValueFunction(metric);
        String unit = getUnit(metric);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip header lines
            for (int i = 0; i < 4; i++) {
                br.readLine();
            }

            double result = aggregate(br, startDate, endDate, columnIndex, selector, aggregationType, parseValue);
            printResult(result, unit, aggregationType);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static int getColumnIndex(String metric) {
        switch (metric) {
            case "temperature_2m": return 1;
            case "pressure_msl": return 3;
            case "wind_speed_10m": return 5;
            case "direct_normal_irradiance_instant": return 8;
            default: throw new IllegalArgumentException("Invalid metric");
        }
    }

    private static Function<String, Double> getParseValueFunction(String metric) {
        return Double::parseDouble;
    }

    private static String getUnit(String metric) {
        switch (metric) {
            case "temperature_2m": return "°C";
            case "pressure_msl": return "hPa";
            case "wind_speed_10m": return "km/h";
            case "direct_normal_irradiance_instant": return "W/m²";
            default: throw new IllegalArgumentException("Invalid metric");
        }
    }

    private static double aggregate(BufferedReader br, LocalDate startDate, LocalDate endDate,
                                    int columnIndex, String selector, String aggregationType,
                                    Function<String, Double> parseValue) throws IOException {
        String line;
        double sum = 0;
        int count = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            LocalDateTime dateTime = LocalDateTime.parse(parts[0], formatter);

            if (dateTime.toLocalDate().isBefore(startDate) || !dateTime.toLocalDate().isBefore(endDate)) {
                continue;
            }

            boolean isDay = "1".equals(parts[7]);
            if ((selector.equals("DAY") && !isDay) || (selector.equals("NIGHT") && isDay)) {
                continue;
            }

            double value = parseValue.apply(parts[columnIndex]);
            sum += value;
            count++;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        switch (aggregationType) {
            case "SUM": return sum;
            case "AVG": return count > 0 ? sum / count : 0;
            case "MIN": return min;
            case "MAX": return max;
            default: throw new IllegalArgumentException("Invalid aggregation type");
        }
    }

    private static void printResult(double result, String unit, String aggregationType) {
        String format = "%.15f %s%n";
        if (result >= 1_000_000) {
            format = "%.1e %s%n";
        } else if (aggregationType.equals("MIN") || aggregationType.equals("MAX")) {
            format = "%.1f %s%n";
        } else {
            format = "%.15f %s%n";
        }
        System.out.printf(format, result, unit);
    }
}
